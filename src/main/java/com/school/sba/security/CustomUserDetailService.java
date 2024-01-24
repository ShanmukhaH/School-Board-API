package com.school.sba.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.school.sba.repoistory.UserRepoistory;

@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepoistory userRepoistory;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return userRepoistory.findByUserName(username).map(user->new CustomUserDetail(user)).orElseThrow(()->new UsernameNotFoundException("Failed to authenticate user"));
	}

}
