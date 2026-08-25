// Hero-only interactions for the landing page: scroll parallax on the backdrop,
// and CTA/nav links that jump to the auth card with the right tab already active.
// Kept separate from login.js (which owns the actual auth logic) on purpose.

const plate = document.querySelector(".landing-hero-bg");
if (plate) {
    const onScroll = () => {
        const y = (window.scrollY || 0) * 0.28;
        plate.style.transform = `translate3d(0, ${y.toFixed(2)}px, 0)`;
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
}

document.querySelectorAll("[data-scroll-tab]").forEach((el) => {
    el.addEventListener("click", () => {
        const tabBtn = document.querySelector(`.auth-tab[data-tab="${el.dataset.scrollTab}"]`);
        if (tabBtn) tabBtn.click();
    });
});
