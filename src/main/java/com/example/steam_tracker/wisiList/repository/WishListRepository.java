package com.example.steam_tracker.wisiList.repository;

import com.example.steam_tracker.wisiList.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
	List<WishList> findAllByAccountId(Long accountId);

	List<WishList> findAllByAppId(Long appId);
}
