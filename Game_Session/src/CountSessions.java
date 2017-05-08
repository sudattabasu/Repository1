
public class CountSessions
{
	long validSession=0,totalSession=0;
	double totalSessionTime=0,averageSessionTime=0;
	CountSessions()
	{}
	CountSessions(long vSession,long tSession,double tSessionTime)
	{
		validSession=vSession;
		totalSession=tSession;
		totalSessionTime=tSessionTime;
	}
	CountSessions(long vSession,long tSession,double tSessionTime,double aSessionTime)
	{
		validSession=vSession;
		totalSession=tSession;
		totalSessionTime=tSessionTime;
		averageSessionTime=aSessionTime;
	}
	public long getTotalSession() {
		return totalSession;
	}
	public void setTotalSession(long totalSession) {
		this.totalSession = totalSession;
	}
	public long getValidSession() {
		return validSession;
	}
	public void setValidSession(long validSession) {
		this.validSession = validSession;
	}
	public double getTotalSessionTime() {
		return totalSessionTime;
	}
	public void setTotalSessionTime(double totalSessionTime) {
		this.totalSessionTime = totalSessionTime;
	}
	public double getAverageSessionTime() {
		return averageSessionTime;
	}
	public void setAverageSessionTime(double averageSessionTime) {
		this.averageSessionTime = averageSessionTime;
	}
	
	
}