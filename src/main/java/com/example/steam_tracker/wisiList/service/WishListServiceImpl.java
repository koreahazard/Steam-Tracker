package com.example.steam_tracker.wisiList.service;

import com.example.steam_tracker.account.entity.Account;
import com.example.steam_tracker.account.repository.AccountRepository;
import com.example.steam_tracker.common.CustomException;
import com.example.steam_tracker.common.ErrorCode;
import com.example.steam_tracker.email.service.EmailService;
import com.example.steam_tracker.game.entity.Game;
import com.example.steam_tracker.game.repository.GameRepository;
import com.example.steam_tracker.wisiList.entity.TargetType;
import com.example.steam_tracker.wisiList.entity.WishList;
import com.example.steam_tracker.wisiList.repository.WishListRepository;
import com.example.steam_tracker.wisiList.service.request.AddWishListRequest;
import com.example.steam_tracker.wisiList.service.response.WishListInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
	private final WishListRepository wishListRepository;
	private final GameRepository gameRepository;
	private final AccountRepository accountRepository;
	private final EmailService emailService;

	@Override
	@Transactional
	public void addWishList(AddWishListRequest request) {
		WishList wishList = new WishList(
				request.getAccountId(),
				request.getAppId(),
				request.getTargetType(),
				request.getTargetValue()
		);
		wishListRepository.save(wishList);
		log.info("위시리스트 등록 완료 - accountId: {}, appId: {}", request.getAccountId(), request.getAppId());
	}

	@Override
	public List<WishListInfoResponse> getWishListInfo(Long accountId) {
		List<WishList> wishLists = wishListRepository.findAllByAccountId(accountId);
		List<WishListInfoResponse> responseList = new ArrayList<>();
		for (WishList wishList : wishLists) {
			Game game = gameRepository.findByAppId(wishList.getAppId())
					.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
			responseList.add(new WishListInfoResponse(wishList, game.getName()));
		}
		return responseList;
	}

	@Override
	@Transactional
	public void deleteWishList(Long accountId, Long wishListId) {
		WishList wishList = wishListRepository.findById(wishListId)
				.orElseThrow(() -> new CustomException(ErrorCode.WISH_LIST_NOT_FOUND));
		if (!wishList.getAccountId().equals(accountId)) {
			throw new CustomException(ErrorCode.WISH_LIST_UNAUTHORIZED);
		}
		wishListRepository.delete(wishList);
		log.info("위시리스트 삭제 완료 - wishListId: {}", wishListId);
	}

	@Override
	@Transactional
	public void checkAndNotify(Long appId, int currentPrice, int discountPercent) {
		List<WishList> wishLists = wishListRepository.findAllByAppId(appId);
		for (WishList wishList : wishLists) {
			int currentValue = wishList.getTargetType() == TargetType.PRICE ? currentPrice : discountPercent;
			boolean isInRange = wishList.getTargetType() == TargetType.PRICE
					? currentValue <= wishList.getTargetValue()
					: currentValue >= wishList.getTargetValue();

			if (!isInRange) {
				wishList.updateInTargetRange(false);
				continue;
			}

			boolean shouldNotify = false;

			if (!wishList.isInTargetRange()) {
				shouldNotify = true;
			} else if (wishList.getLastTriggeredValue() != null) {
				if (wishList.getTargetType() == TargetType.PRICE && currentValue < wishList.getLastTriggeredValue()) {
					shouldNotify = true;
				} else if (wishList.getTargetType() == TargetType.DISCOUNT && currentValue > wishList.getLastTriggeredValue()) {
					shouldNotify = true;
				}
			}

			if (shouldNotify) {
				wishList.updateTriggered(currentValue);
				Account account = accountRepository.findById(wishList.getAccountId())
						.orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
				Game game = gameRepository.findByAppId(appId)
						.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
				emailService.sendWishListAlert(account.getEmail(), game.getName(), wishList.getTargetType(), currentValue);
				log.info("알림 발송 - email: {}, game: {}, value: {}", account.getEmail(), game.getName(), currentValue);
			}
		}
	}


}
