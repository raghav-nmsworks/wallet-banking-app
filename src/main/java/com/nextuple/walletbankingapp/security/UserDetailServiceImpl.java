package com.nextuple.walletbankingapp.security;

import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(username).get();
            return user;
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("user not found with username : " + username);
        }
    }
}
