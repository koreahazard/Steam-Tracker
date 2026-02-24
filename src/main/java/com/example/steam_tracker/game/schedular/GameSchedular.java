package com.example.steam_tracker.game.schedular;

import com.example.steam_tracker.game.service.GameService;
import com.example.steam_tracker.steam.facade.SteamCollectorFacade;
import com.example.steam_tracker.steam.facade.request.CollectGameDataRequest;
import com.example.steam_tracker.steam.facade.request.CollectPriceDataRequest;
import com.example.steam_tracker.steam.facade.request.ExpandGameDataRequest;
import com.example.steam_tracker.steam.facade.response.CollectGameDataResponse;
import com.example.steam_tracker.steam.facade.response.CollectPriceDataResponse;
import com.example.steam_tracker.steamIndex.service.SteamIndexService;
import com.example.steam_tracker.wisiList.service.WishListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameSchedular {

	private final SteamCollectorFacade steamCollectorFacade;
	private final GameService gameService;
	private final SteamIndexService steamIndexService;
	private final WishListService wishListService;

	private final ReentrantLock collectLock = new ReentrantLock();

	@EventListener(ApplicationReadyEvent.class)
	public void initialCollect() {

		if (!gameService.isEmpty()) {
			log.info("초기 데이터가 이미 존재하여 수집을 건너뜁니다.");
			return;
		}

		if (!collectLock.tryLock()) {
			log.info("이미 수집 작업이 실행 중입니다. initialCollect 스킵");
			return;
		}

		try {
			int startRankIndex = 0;
			int totalCount = 100;

			CollectGameDataRequest request =
					new CollectGameDataRequest(startRankIndex, totalCount);

			List<CollectGameDataResponse> data =
					steamCollectorFacade.collectGameData(request);

			gameService.saveInitData(data);

			List<Long> targetAppIdList = gameService.getTrackingAppIds();
			steamIndexService.recordDailyIndex(targetAppIdList);

			log.info("스팀 가격 지수 업데이트 완료");

		} finally {
			collectLock.unlock();
		}
	}

	@Scheduled(cron = "0 */5 * * * *")
	public void periodicCollect() {

		if (!collectLock.tryLock()) {
			log.info("이미 수집 작업이 실행 중입니다. periodicCollect 스킵");
			return;
		}

		try {

			log.info("주기적 업데이트 스케줄러 시작");
			int minimumConstituentCount = 100;
			int startRankIndex = 200;
			int totalCount = 100;

			List<Long> targetAppIdList = gameService.getTrackingAppIds();

			if (targetAppIdList.size() < minimumConstituentCount) {
				ExpandGameDataRequest request =
						new ExpandGameDataRequest(targetAppIdList, startRankIndex, totalCount);

				List<CollectGameDataResponse> data =
						steamCollectorFacade.expandGameData(request);

				long randomDelay =
						ThreadLocalRandom.current().nextLong(2000, 5001);

				log.info("Rate Limit 회피를 위해 {}ms 동안 대기합니다...", randomDelay);
				Thread.sleep(randomDelay);

				gameService.saveInitData(data);
			}

			if (targetAppIdList.isEmpty()) {
				log.info("추적 중인 게임이 없어 스케줄러를 종료합니다.");
				return;
			}

			CollectPriceDataRequest request =
					new CollectPriceDataRequest(targetAppIdList);

			List<CollectPriceDataResponse> priceDataList =
					steamCollectorFacade.collectPriceData(request);

			gameService.updatePriceData(priceDataList);
			log.info("가격 정보 업데이트 완료");

			for (CollectPriceDataResponse price : priceDataList) {
				wishListService.checkAndNotify(
						price.getAppId(),
						price.getCurrentPrice(),
						price.getDiscountPercent()
				);
			}

			log.info("위시리스트 알림 체크 완료");

			targetAppIdList = gameService.getTrackingAppIds();
			steamIndexService.recordDailyIndex(targetAppIdList);
			log.info("스팀 가격 지수 업데이트 완료");

		} catch (Exception e) {
			log.error("가격 정보 업데이트 중 오류 발생: {}", e.getMessage());
		} finally {
			collectLock.unlock();
		}
	}
}