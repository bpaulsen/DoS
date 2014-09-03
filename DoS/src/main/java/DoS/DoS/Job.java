package DoS.DoS;

import java.time.LocalDateTime;

public class Job {
	private boolean is_running = false;
	private LocalDateTime create_timestamp;
	private String id;
	
	public Job( String id ) {
		this.id = id;  // do I need a copy of this?
		is_running = false;
		create_timestamp = LocalDateTime.now();
	}
	
	public Job( String id, LocalDateTime create_timestamp ) {
		this.id = id;
		this.create_timestamp = create_timestamp;
		is_running = false;
	}
	
	public LocalDateTime get_create_timestamp() {
		return create_timestamp;
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
