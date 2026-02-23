package com.example.steam_tracker.account.controller.requestForm;

import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAccountRequestForm {
	private String username;
	private String password;
	private String email;
	private String nickname;

	public SignUpAccountRequest toSignUpAccountRequest() {
		return new SignUpAccountRequest(
				this.username,
				this.password,
				this.email,
				this.nickname
		);
	}
}
