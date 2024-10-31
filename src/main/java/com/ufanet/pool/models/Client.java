package com.ufanet.pool.models;


import com.ufanet.pool.models.dto.ClientAddRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String email;


    public Client(ClientAddRequest clientAddRequest) {
        this.name = clientAddRequest.getName();
        this.phone = clientAddRequest.getPhone();
        this.email = clientAddRequest.getEmail();
    }


}
