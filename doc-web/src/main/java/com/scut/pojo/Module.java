package com.scut.pojo;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Module implements Serializable{

	private static final long serialVersionUID = 4735154951856711175L;
	private int id;
	private String name;
	private String description;
	private int pid;
	private String path;
	private String filetype;
	private String attachtype;
	private Date dateline;
	private String statu;
	private Timestamp createtime;
	public Module(){}
	public Module(String name, String description, int pid, String filetype, String attachtype,
			Date dateline) {
		this.name = name;
		this.description = description;
		this.pid = pid;
		this.filetype = filetype;
		this.attachtype = attachtype;
		this.dateline = dateline;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public String getAttachtype() {
		return attachtype;
	}
	public void setAttachtype(String attachtype) {
		this.attachtype = attachtype;
	}
	public Date getDateline() {
		return dateline;
	}
	public void setDateline(Date dateline) {
		this.dateline = dateline;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	@Override
	public String toString() {
		return "Module [id=" + id + ", name=" + name + ", description=" + description + ", pid=" + pid + ", path="
				+ path + ", filetype=" + filetype + ", attachtype=" + attachtype + ", dateline=" + dateline + "]";
	}
}
