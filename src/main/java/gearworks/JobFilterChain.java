package gearworks;

import java.util.ArrayList;
import java.util.List;

public class JobFilterChain implements JobFilterInterface {

	private List<JobFilterInterface> filters;

	public JobFilterChain() {

		this.filters = new ArrayList<>();
	}

	public void addFilter(JobFilterInterface filter) {

		this.filters.add(filter);
	}

	@Override
	public boolean apply(Job job) {

		for (JobFilterInterface filter : filters) {

			if (!filter.apply(job)) {
				
				return false;
			}
		}
		return true;
	}
}
