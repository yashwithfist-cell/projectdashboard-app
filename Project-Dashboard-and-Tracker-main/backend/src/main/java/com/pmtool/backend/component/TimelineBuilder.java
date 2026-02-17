package com.pmtool.backend.component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pmtool.backend.DTO.TimelineRowDTO;
import com.pmtool.backend.DTO.WorkLogResponseDTO;
import com.pmtool.backend.DTO.response.IdleLogDtoResponse;
import com.pmtool.backend.util.TimeUtil;

@Component
public class TimelineBuilder {

	public List<TimelineRowDTO> build(List<LocalDateTime> checkIns, List<LocalDateTime> checkOuts,
			List<IdleLogDtoResponse> idleLogs, List<WorkLogResponseDTO> projectLogs, LocalDateTime now) {

		List<TimelineRowDTO> rows = new ArrayList<>();
		int nowMin = TimeUtil.toMinutes(now);

		for (int i = 0; i < checkIns.size(); i++) {

			int inMin = TimeUtil.toMinutes(checkIns.get(i));
			int outMin = (i < checkOuts.size() && checkOuts.get(i) != null) ? TimeUtil.toMinutes(checkOuts.get(i))
					: nowMin;

			// 1️⃣ Check-in marker
			push(rows, "Check-in", inMin, inMin, "Check-in");

			// 2️⃣ Projects / Idle inside this session
			int tempStart = inMin; // matches JS structure

			for (WorkLogResponseDTO proj : projectLogs) {

				int projStart = TimeUtil.toMinutes(LocalDate.now().atTime(proj.getStartTime()));
				int projEnd = proj.getEndTime() != null ? TimeUtil.toMinutes(LocalDate.now().atTime(proj.getEndTime()))
						: nowMin;

				int start = Math.max(tempStart, projStart);
				int end = Math.min(outMin, projEnd);

				if (end <= start)
					continue;

				// ----- Idle blocks inside project -----
				List<int[]> idleIntervals = idleLogs.stream().map(idle -> {
					int s = Math.max(start, TimeUtil.toMinutes(idle.getIdleStartTime()));
					int e = Math.min(end,
							idle.getIdleEndTime() != null ? TimeUtil.toMinutes(idle.getIdleEndTime()) : nowMin);
					return new int[] { s, e };
				}).filter(a -> a[1] > a[0]).sorted(Comparator.comparingInt(a -> a[0])).toList();

				int pointer = start;

				for (int[] idle : idleIntervals) {
					if (idle[0] > pointer) {
						// Project work before idle
						push(rows, "Project", pointer, idle[0],
								proj.getProjectName() + "/" + proj.getMilestoneName() + "/" + proj.getDisciplineName());
					}

					// Idle
					push(rows, "Idle", idle[0], idle[1], "Idle");
					pointer = idle[1];
				}

				// Remaining project time
				if (pointer < end) {
					push(rows, "Project", pointer, end,
							proj.getProjectName() + "/" + proj.getMilestoneName() + "/" + proj.getDisciplineName());
				}
			}

			// 4️⃣ Checked-out gap
			if (i < checkOuts.size() && checkOuts.get(i) != null) {
				int gapStart = outMin;
				int gapEnd = (i + 1 < checkIns.size()) ? TimeUtil.toMinutes(checkIns.get(i + 1)) : nowMin;

				if (gapEnd > gapStart) {
					push(rows, "Checked Out", gapStart, gapEnd, "Checked Out");
				}
			}
		}

		rows.sort(Comparator.comparingInt(TimelineRowDTO::getStartMin));
		return rows;
	}

	private void push(List<TimelineRowDTO> rows, String type, int start, int end, String label) {

		if (end < start)
			return;

		TimelineRowDTO r = new TimelineRowDTO();
		r.setType(type);
		r.setStartMin(start);
		r.setEndMin(end);
		r.setStart(TimeUtil.toTime(start));
		r.setEnd(TimeUtil.toTime(end));
		r.setDuration(TimeUtil.formatDuration(end - start));
		r.setLabel(label);

		rows.add(r);
	}
}
