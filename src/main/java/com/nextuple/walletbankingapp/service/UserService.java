package com.nextuple.walletbankingapp.service;

import com.nextuple.walletbankingapp.dto.request.LoginRequestDTO;
import com.nextuple.walletbankingapp.dto.response.LoginResponseDTO;
import com.nextuple.walletbankingapp.dto.response.RegistrationResponseDTO;
import com.nextuple.walletbankingapp.dto.response.UserDTO;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.excepiton.UserAlreadyExistsException;
import com.nextuple.walletbankingapp.excepiton.UserNotFoundException;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.repository.UserRepository;
import com.nextuple.walletbankingapp.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    private EmailSenderService emailSenderService;
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public Optional<User> getUserById(String id) {
//        return userRepository.findById(id);
//    }

    public RegistrationResponseDTO createUser(User user) {

        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()){
            //Todo exception
            throw new UserAlreadyExistsException("Email address already exists : " + user.getEmail());
        }
        String rawPass = user.getPassword();
        Wallet wallet = new Wallet();
        wallet.setId(user.getEmail());
        wallet.setBalance(0.0);
        walletRepository.save(wallet);
        user.setWallet(wallet);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        String emailBody = "Wallet Account Created SuccessFully." +
                "\nEmail: " + user.getEmail()+
                "\nPassword: " + user.getPassword();
        emailSenderService.sendSimpleEmail(user.getEmail(),emailBody,"Wallet created successfully");
        return new RegistrationResponseDTO(savedUser.getEmail(),jwtHelper.generateToken(savedUser));
    }

    /*public User updateUser(String id, User updatedUser) {
        if (userRepository.existsById(id)) {
            updatedUser.setId(id);
            return userRepository.save(updatedUser);
        }
        throw new UsernameNotFoundException("User not exits with this email : " );
    }*/

    /*public void deleteUser(String id) {
        userRepository.deleteById(id);
    }*/

    public User getUserByEmail(String email){
        if (userRepository.findByEmail(email).isPresent())
            return userRepository.findByEmail(email).get();
        throw new UsernameNotFoundException("User not exists");
    }

    public UserDTO getUserDTOByEmail(String email){

        if (userRepository.findByEmail(email).isPresent()){
            User user = userRepository.findByEmail(email).get();
            UserDTO userDTO = new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getWallet());
            return userDTO;
        }
        throw new UserNotFoundException("User not exists");
    }

    public LoginResponseDTO login(LoginRequestDTO loginDTO){
        User user = getUserByEmail(loginDTO.getEmail());
        System.out.println(user+"user.....");
        if (user != null){
            //if (user.getPassword().equals(loginDTO.getPassword())){
            if (passwordEncoder.matches(loginDTO.getPassword(),user.getPassword())){
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO(user.getEmail(), jwtHelper.generateToken(user));
                return loginResponseDTO;
            }
        }
        throw new UsernameNotFoundException("User not registered");
    }
}

