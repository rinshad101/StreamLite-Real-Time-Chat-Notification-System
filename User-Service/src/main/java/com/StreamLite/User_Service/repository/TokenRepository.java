package com.StreamLite.User_Service.repository;

import com.StreamLite.User_Service.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
