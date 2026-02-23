package com.example.steam_tracker.wisiList.controller;

import com.example.steam_tracker.account.service.AccountService;
import com.example.steam_tracker.common.ResponseForm;
import com.example.steam_tracker.wisiList.controller.requestForm.AddWishListRequestForm;
import com.example.steam_tracker.wisiList.service.WishListService;
import com.example.steam_tracker.wisiList.service.request.AddWishListRequest;
import com.example.steam_tracker.wisiList.service.response.WishListInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish-list")
public class WishListController {
	private final WishListService wishListService;
	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<ResponseForm<Void>> addWishList(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody AddWishListRequestForm form
	) {
		String token = authHeader.replace("Bearer ", "");
		Long accountId = accountService.getAccountIdFromToken(token, "access");
		AddWishListRequest request = new AddWishListRequest(
				accountId,
				form.getAppId(),
				form.getTargetType(),
				form.getTargetValue()
		);
		wishListService.addWishList(request);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"위시리스트 등록 완료",
						null));
	}

	@GetMapping
	public ResponseEntity<ResponseForm<List<WishListInfoResponse>>> getWishListInfo(
			@RequestHeader("Authorization") String authHeader
	) {
		String token = authHeader.replace("Bearer ", "");
		Long accountId = accountService.getAccountIdFromToken(token, "access");
		List<WishListInfoResponse> response = wishListService.getWishListInfo(accountId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"위시리스트 조회 성공",
						response));
	}

	@DeleteMapping("/{wishListId}")
	public ResponseEntity<ResponseForm<Void>> deleteWishList(
			@RequestHeader("Authorization") String authHeader,
			@PathVariable Long wishListId
	) {
		String token = authHeader.replace("Bearer ", "");
		Long accountId = accountService.getAccountIdFromToken(token, "access");
		wishListService.deleteWishList(accountId, wishListId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ResponseForm.success(
						"SUCCESS",
						"위시리스트 삭제 완료",
						null));
	}
}
