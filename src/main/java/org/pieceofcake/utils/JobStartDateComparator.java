package org.pieceofcake.utils;

import java.util.Comparator;

import org.pieceofcake.objects.Job;

public class JobStartDateComparator implements Comparator<Job<?>> {

	@Override
	public int compare(Job<?> job1, Job<?> job2) {
		return job1.getStart().compareTo(job2.getStart());
	}

}
