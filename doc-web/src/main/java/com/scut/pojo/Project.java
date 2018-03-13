package com.scut.pojo;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Project implements Serializable{
	private static final long serialVersionUID = -7644763991514824984L;
	private int id;
	private String name;
	private Date dateline;
	private int uid;
	private List<Integer> ids;
	private String path;
	private Timestamp createtime;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateline() {
		return dateline;
	}
	public void setDateline(Date dateline) {
		this.dateline = dateline;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public List<Integer> getIds() {
		return ids;
	}
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", dateline=" + dateline + ", uid=" + uid + ", ids=" + ids
				+ ", path=" + path + ", createtime=" + createtime + "]";
	}
}
