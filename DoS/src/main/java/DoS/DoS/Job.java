package DoS.DoS;

import java.time.LocalDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault

public class Job implements Comparable<Object> {
	private boolean is_running = false;
	private LocalDateTime create_timestamp;
	private String id;
	private String site;
	private int priority;
	
	// should we allow null sites or null ids?
	public Job( String site, String id, int priority ) {
		this.site = site;
		this.id = id;
		this.priority = priority; // might need to do some verification on this (i.e., priorities 0-10)
		is_running = false;
		create_timestamp = LocalDateTime.now();
		verify_parameters(site, id, priority, create_timestamp);
	}
	
	public Job( String site, String id, int priority, LocalDateTime create_timestamp ) {
		this.site = site;
		this.id = id;
		this.priority = priority;
		this.create_timestamp = create_timestamp;
		is_running = false;
		verify_parameters(site, id, priority, create_timestamp);
	}
	
	public Job( String site, String id, int priority, boolean is_running ) {
		this.site = site;
		this.id = id;
		this.priority = priority;
		this.is_running = is_running;
		create_timestamp = LocalDateTime.now();
		verify_parameters(site, id, priority, create_timestamp);
	}
	
	public Job( String site, String id, int priority, LocalDateTime create_timestamp, boolean is_running ) {
		this.site = site;
		this.id = id;
		this.priority = priority;
		this.create_timestamp = create_timestamp;
		this.is_running = is_running;
		verify_parameters(site, id, priority, create_timestamp);
	}
	
	private static void verify_parameters( String site, String id, int priority, LocalDateTime create_timestamp) {
		if (site == null) {
			throw new IllegalArgumentException("null site is not allowed");
		}
		if (id == null) {
			throw new IllegalArgumentException("null id is not allowed");
		}
		if (create_timestamp == null) {
			throw new IllegalArgumentException("null create_timestamp is not allowed");
		}
		if (priority < 0 || priority > 10) {
			throw new IllegalArgumentException("priority must be between 0 and 10 inclusive");
		}
	}
	
	public LocalDateTime get_create_timestamp() {
		return create_timestamp;
	}
	
	public String get_site() {
		return site;
	}
	
	public String get_id() {
		return id;
	}
	
	public boolean get_is_running() {
		return is_running;
	}
	
	public int get_priority() {
		return priority;
	}
	
	public void set_is_running(boolean is_running) {
		this.is_running = is_running;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object obj) {
		if (this.equals(obj)) {
			return 0;
		}
		if (obj == null) {
			return -1;
		}

		Job other = (Job) obj;		
		// sort descending on priority
		if ( other.priority - priority != 0) {
			return other.priority - priority; 
		}
		
		// sort ascending on create_timestamp
		if ( !create_timestamp.equals(other.create_timestamp) ) {
     		return create_timestamp.compareTo(other.create_timestamp);
     	}
		
		// sort ascending on site name
		if ( !site.equals(other.site) ) {
     		return site.compareTo(other.site);
     	}		
		
		// sort ascending on id
		return id.compareTo(other.id);
	}
}
