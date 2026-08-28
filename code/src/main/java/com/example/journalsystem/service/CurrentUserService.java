package com.example.journalsystem.service;

import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.entities.StaffEmployment;
import com.example.journalsystem.entities.UserAccount;
import com.example.journalsystem.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/// Slår upp den inloggade användaren och hens anställning.
///
/// Den inloggade principalen bär bara användarnamn och rollsträng — avdelningen
/// finns inte i säkerhetskontexten. Den här klassen samlar uppslagningen som
/// tidigare låg utspridd i UserAccountService och AuditLogService, så att
/// behörighetsreglerna i US-2 och US-51 har en enda källa.
@Service
public class CurrentUserService {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /// Användarnamnet för den inloggade, eller tomt om ingen är inloggad.
    public Optional<String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    /// True om den inloggade har den angivna behörigheten.
    /// Rollsträngen ska inkludera ROLE_-prefixet, precis som i role-tabellen.
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority granted : authentication.getAuthorities()) {
            if (authority.equals(granted.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /// Id för den avdelning den inloggade är anställd på.
    /// Tomt för konton utan anställning, t.ex. patientkonton.
    ///
    /// Obs: en anställd kan i teorin ha flera anställningar. Precis som
    /// UserDetailsServiceImpl och UserAccountService använder vi den första.
    @Transactional(readOnly = true)
    public Optional<Long> getDepartmentId() {
        return currentEmployment().map(e -> e.getDepartment().getId());
    }

    /// Id för den anställde bakom det inloggade kontot.
    @Transactional(readOnly = true)
    public Optional<Long> getStaffId() {
        return getUsername()
                .flatMap(userAccountRepository::findByUsername)
                .map(UserAccount::getStaff)
                .map(Staff::getId);
    }

    @Transactional(readOnly = true)
    protected Optional<StaffEmployment> currentEmployment() {
        return getUsername()
                .flatMap(userAccountRepository::findByUsername)
                .map(UserAccount::getStaff)
                .filter(staff -> staff != null && !staff.getStaffEmployment().isEmpty())
                .map(staff -> staff.getStaffEmployment().get(0))
                .filter(employment -> employment.getDepartment() != null);
    }
}
