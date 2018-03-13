package com.scut.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class User implements Serializable{
	private static final long serialVersionUID = -4622167997680755121L;
	private int id;
	private String account;
	private String name;
	private String unit;
	private int state;//用户状态，默认是0，即在校就读。
	private String password;
	private Timestamp ctime;
	private Role role;//角色
	private Integer leaderId;
	private List<Integer> leaders;
	public User(){}
	public User(String account,String name,String unit){
		this.account=account;
		this.name=name;
		this.unit=unit;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Timestamp getCtime() {
		return ctime;
	}
	public void setCtime(Timestamp ctime) {
		this.ctime = ctime;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Integer getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(Integer leaderId) {
		this.leaderId = leaderId;
	}
	public List<Integer> getLeaders() {
		return leaders;
	}
	public void setLeaders(List<Integer> leaders) {
		this.leaders = leaders;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", account=" + account + ", name=" + name + ", unit=" + unit + ", state=" + state
				+ ", password=" + password + ", ctime=" + ctime + ", role=" + role + ", leaderId=" + leaderId
				+ ", leaders=" + leaders + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
