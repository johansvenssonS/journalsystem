// Pure-render weekly calendar grid. No fetching, no state — callers own the data and re-render
// this on change. `AppointmentDto` has no duration field, so every block renders at a fixed
// visual length; overlapping appointments on the same day are split into side-by-side lanes.

import { escapeHtml, statusBadgeClass } from "../ui.js";

const START_HOUR = 7;
const END_HOUR = 19;
const HOUR_HEIGHT = 56; // px
const BLOCK_MINUTES = 40; // visual block length — appointments have no explicit duration
const DAY_LABELS = ["Mån", "Tis", "Ons", "Tors", "Fre", "Lör", "Sön"];

export function startOfWeek(d = new Date()) {
    const date = new Date(d);
    const day = date.getDay(); // 0 = Sunday
    const diff = day === 0 ? -6 : 1 - day; // shift to Monday
    date.setDate(date.getDate() + diff);
    date.setHours(0, 0, 0, 0);
    return date;
}

export function addDays(d, n) {
    const date = new Date(d);
    date.setDate(date.getDate() + n);
    return date;
}

function assignLanes(dayAppointments) {
    // Greedy interval-lane assignment so overlapping appointments render side by side.
    const sorted = [...dayAppointments].sort((a, b) => new Date(a.scheduledAt) - new Date(b.scheduledAt));
    const laneEndTimes = []; // end time (ms) currently occupying each lane
    const placed = [];

    sorted.forEach((appt) => {
        const start = new Date(appt.scheduledAt).getTime();
        const end = start + BLOCK_MINUTES * 60000;
        let lane = laneEndTimes.findIndex((endTime) => endTime <= start);
        if (lane === -1) {
            lane = laneEndTimes.length;
        }
        laneEndTimes[lane] = end;
        placed.push({ appt, lane, start, end });
    });

    // Overlap-cluster width: for each appointment, how many lanes are active during its span.
    placed.forEach((p) => {
        p.totalLanes = placed.filter((o) => o.start < p.end && o.end > p.start).reduce((max, o) => Math.max(max, o.lane + 1), 1);
    });

    return placed;
}

function blockStyle(p) {
    const d = new Date(p.start);
    const minutesFromStart = (d.getHours() - START_HOUR) * 60 + d.getMinutes();
    const top = Math.max(0, (minutesFromStart / 60) * HOUR_HEIGHT);
    const height = (BLOCK_MINUTES / 60) * HOUR_HEIGHT - 2;
    const widthPct = 100 / p.totalLanes;
    const leftPct = widthPct * p.lane;
    return `top:${top}px;height:${height}px;width:calc(${widthPct}% - 4px);left:calc(${leftPct}% + 2px);`;
}

export function renderWeekCalendar(container, { appointments, weekStart, staffName, deptName }) {
    const days = Array.from({ length: 7 }, (_, i) => addDays(weekStart, i));
    const hours = Array.from({ length: END_HOUR - START_HOUR }, (_, i) => START_HOUR + i);
    const totalHeight = hours.length * HOUR_HEIGHT;
    const today = new Date();
    const isToday = (d) => d.toDateString() === today.toDateString();

    const byDay = days.map((day) =>
        appointments.filter((a) => a.scheduledAt && new Date(a.scheduledAt).toDateString() === day.toDateString())
    );

    container.innerHTML = `
        <div class="cal-grid">
            <div class="cal-gutter">
                <div class="cal-gutter-head"></div>
                ${hours.map((h) => `<div class="cal-hour-label" style="height:${HOUR_HEIGHT}px;">${String(h).padStart(2, "0")}:00</div>`).join("")}
            </div>
            ${days.map((day, i) => `
                <div class="cal-day-col">
                    <div class="cal-day-head ${isToday(day) ? "cal-day-head-today" : ""}">
                        <span class="cal-day-name">${DAY_LABELS[i]}</span>
                        <span class="cal-day-date">${day.getDate()}/${day.getMonth() + 1}</span>
                    </div>
                    <div class="cal-day-body" style="height:${totalHeight}px;">
                        ${hours.map((_, hi) => `<div class="cal-hour-row" style="top:${hi * HOUR_HEIGHT}px;height:${HOUR_HEIGHT}px;"></div>`).join("")}
                        ${assignLanes(byDay[i]).map((p) => calBlock(p.appt, blockStyle(p), staffName, deptName)).join("")}
                    </div>
                </div>
            `).join("")}
        </div>
    `;
}

function calBlock(a, style, staffName, deptName) {
    const time = a.scheduledAt ? new Date(a.scheduledAt).toLocaleTimeString("sv-SE", { hour: "2-digit", minute: "2-digit" }) : "-";
    const who = staffName ? staffName(a.staffId) : "";
    const dept = deptName ? deptName(a.departmentId) : "";
    return `
        <div class="cal-block cal-block-${statusBadgeClass(a.status).replace("badge-", "")}" title="${escapeHtml(time + " · Patient #" + a.patientId + (a.note ? " · " + a.note : ""))}" style="${style}">
            <span class="cal-block-time">${time}</span>
            <span class="cal-block-title">Patient #${escapeHtml(String(a.patientId ?? "-"))}</span>
            ${who ? `<span class="cal-block-sub">${escapeHtml(who)}</span>` : dept ? `<span class="cal-block-sub">${escapeHtml(dept)}</span>` : ""}
        </div>
    `;
}

export function weekRangeLabel(weekStart) {
    const end = addDays(weekStart, 6);
    const fmt = (d) => `${d.getDate()}/${d.getMonth() + 1}`;
    return `${fmt(weekStart)} – ${fmt(end)}`;
}
