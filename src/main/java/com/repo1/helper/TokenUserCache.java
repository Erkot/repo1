package com.repo1.helper;

import com.repo1.entity.User;
import java.util.HashMap;
import java.util.Map;


public class TokenUserCache {
    
    	private static TokenUserCache tokenUserCache;
        public static Map tokenMap = new HashMap();

    	public static synchronized TokenUserCache getInstance() {
		if (tokenUserCache == null) {
			tokenUserCache = new TokenUserCache();
		}
		return tokenUserCache;
	}
        
        public static void putUserOnToken(String token, User user){
            tokenMap.put(token, user);
        }
        
        public static User getUserByToken(String token){
            return (User) tokenMap.get(token);
        }
}
