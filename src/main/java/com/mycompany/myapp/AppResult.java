package com.mycompany.myapp;

public class AppResult {
	public int error = AppError.UNDEFINED;
	public String errorS = AppError.getS(AppError.UNDEFINED);
	public long uid = -1;

	public static AppResult loadFromString(String json) {
		AppResult result = new JsonTransformer().parse(json, AppResult.class);
		return result;
	}

	public String toString() {
		return new JsonTransformer().render(this);
	}

	public AppResult() {

	}

	public AppResult(int error) {
		setError(error);
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public AppResult(int error, long uid) {
		setError(error);
		setUid(uid);
	}

	public void setError(int error) {
		this.error = error;
		this.errorS = AppError.getS(this.error);
	}
}
