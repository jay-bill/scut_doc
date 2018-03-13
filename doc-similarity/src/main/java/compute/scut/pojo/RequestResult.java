package compute.scut.pojo;

public class RequestResult {
	private String code;
	private String msg;
	private String res;
	public RequestResult(){}
	public RequestResult(String code,String msg,String res){
		this.code = code;
		this.msg = msg;
		this.res = res;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
}
