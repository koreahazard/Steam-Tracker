package com.example.steam_tracker.account.controller.requestForm;

import com.example.steam_tracker.account.service.request.LoginAccountRequest;
import com.example.steam_tracker.account.service.request.SignUpAccountRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginAccountRequestForm {
	private String username;
	private String password;

	public LoginAccountRequest toLoginAccountRequest() {
		return new LoginAccountRequest(
				this.username,
				this.password
		);
	}
}
