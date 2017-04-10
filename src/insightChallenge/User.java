package insightChallenge;

import java.util.Objects;

public class User implements Comparable<User>
{
	private String ipAddress;
	private String loginTime;
	private Long bandwidth;
	private Integer statusCode;
	private String url;



	public User(String ipAddress, String loginTime, Long bandwidth, Integer statusCode, String url) {
		super();
		this.ipAddress = ipAddress;
		this.loginTime = loginTime;
		this.bandwidth = bandwidth;
		this.statusCode = statusCode;
		this.url = url;
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
	public Long getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(Long bandwidth) {
		this.bandwidth = bandwidth;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public boolean equals(User obj) {
		// TODO Auto-generated method stub
		return this.bandwidth==(obj.bandwidth);
	}

	@Override 
	public String toString() 
	{ 
		return String.format("%s(%s,%d)",ipAddress, bandwidth, loginTime,statusCode,url); 

	} 
	@Override 
	public int hashCode() 
	{
		int hash = 7; 
		hash = 79 * hash + Objects.hashCode(this.ipAddress);
		hash = 79 * hash + Objects.hashCode(this.bandwidth); 
		hash = 79 * hash + Objects.hashCode(this.loginTime);
		hash = 79 * hash + Objects.hashCode(this.statusCode); 
		hash = 79 * hash + Objects.hashCode(this.url);
		
		return hash; 
	}
	@Override 
	public boolean equals(Object obj) 
	{ 
		if (obj == null) 
		{
			return false; 
		}
		if (getClass() != obj.getClass()) 
		{ return false; 
		}
		final User other = (User) obj; 
		if (!Objects.equals(this.bandwidth, other.bandwidth))
		{
			return false;
		}
		if (!Objects.equals(this.ipAddress, other.ipAddress))
		{
			return false;
		} 
		if (!Objects.equals(this.loginTime, other.loginTime))
		{
			return false;
		}
		if (!Objects.equals(this.statusCode, other.statusCode))
		{
			return false;
		}
		if (!Objects.equals(this.url, other.url))
		{
			return false;
		} 
		return true; 
	}

	@Override
	public int compareTo(User o) {
		// TODO Auto-generated method stub
		return this.bandwidth.compareTo(o.bandwidth);
	}

}
