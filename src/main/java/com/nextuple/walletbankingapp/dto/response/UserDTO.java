package com.nextuple.walletbankingapp.dto.response;

import com.nextuple.walletbankingapp.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    @DBRef
    private Wallet wallet;
}
