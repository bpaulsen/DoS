package DoS.DoS;

import java.time.LocalDateTime;

public class Job {
	private boolean is_running = false;
	private LocalDateTime create_timestamp;
	private String id;
	private String site;
	
	public Job( String site, String id ) {
		this.site = site;  // do I need a copy of this?
		this.id = id;  // do I need a copy of this?
		is_running = false;
		create_timestamp = LocalDateTime.now();
	}
	
	public Job( String site, String id, LocalDateTime create_timestamp ) {
		this.site = site;
		this.id = id;
		this.create_timestamp = create_timestamp;
		is_running = false;
	}
	
	public Job( String site, String id, boolean is_running ) {
		this.site = site;
		this.id = id;
		this.is_running = is_running;
		create_timestamp = LocalDateTime.now();
	}
	
	public Job( String site, String id, LocalDateTime create_timestamp, boolean is_running ) {
		this.site = site;
		this.id = id;
		this.create_timestamp = create_timestamp;
		this.is_running = is_running;
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
	
	public void set_is_running(boolean is_running) {
		this.is_running = is_running;
	}
}
