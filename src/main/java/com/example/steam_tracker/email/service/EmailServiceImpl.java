package com.example.steam_tracker.email.service;

import com.example.steam_tracker.wisiList.entity.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;

	@Override
	public void sendWishListAlert(String email, String gameName, TargetType targetType, int currentValue) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[Steam Tracker] " + gameName + " 가격 알림");

		String body;
		if (targetType == TargetType.PRICE) {
			body = gameName + " 게임의 현재 가격이 " + currentValue + "원으로 목표 가격에 도달했습니다.";
		} else {
			body = gameName + " 게임의 현재 할인율이 " + currentValue + "%로 목표 할인율에 도달했습니다.";
		}

		message.setText(body);
		javaMailSender.send(message);
		log.info("이메일 발송 완료 - to: {}, game: {}", email, gameName);
	}

}
