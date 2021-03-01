package com.tiagods.obrigacoes.service;

import com.tiagods.obrigacoes.dto.UsuarioDTO;
import com.tiagods.obrigacoes.exception.UsuarioNotFoundException;
import com.tiagods.obrigacoes.repository.UsuarioRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UsuarioDTO> dto = repository.findByEmailAndAtivo(email, true);
        if(dto.isPresent()) {
            return new org.springframework.security.core.userdetails.User(dto.get().getEmail(), dto.get().getSenha(),
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuario não encontrado");
        }
    }

    public UsuarioDTO salvar(UsuarioDTO usuario, String senhaAdmin) throws UsuarioNotFoundException {
        UsuarioDTO newUser = new UsuarioDTO();

        Optional<UsuarioDTO> resultEmail = repository.findByEmail(usuario.getEmail());
        if(resultEmail.isPresent()){
            throw new UsuarioNotFoundException("Esse login ja existe");
        }

        Optional<UsuarioDTO> result = repository.findByPerfil(UsuarioDTO.Perfil.ADMIN);
        if(!result.isPresent()){
            String generatedString = RandomStringUtils.random(50, true, true);
            newUser.setPerfil(UsuarioDTO.Perfil.ADMIN);
            newUser.setTokenAdmin(generatedString);
        }
        else if(result.isPresent() && (senhaAdmin == null || senhaAdmin.length() == 0)) {
            throw new UsuarioNotFoundException("Precisa fornecer uma senha de administrador para criar um novo usuario");
        }
        else if(result.isPresent() && !result.get().getTokenAdmin().equals(senhaAdmin)) {
            throw new UsuarioNotFoundException("A senha de administrador nao confere");
        } else {
            //funcao para evitar o reuso do mesmo token
            String generatedString = RandomStringUtils.random(50, true, true);
            UsuarioDTO admin = result.get();
            admin.setTokenAdmin(generatedString);
            repository.save(admin);
        }
        newUser.setNome(usuario.getNome());
        newUser.setEmail(usuario.getEmail());
        newUser.setSenha(bcryptEncoder.encode(usuario.getSenha()));
        newUser.setAtivo(true);
        newUser = repository.save(newUser);
        newUser.setSenha(null);
        newUser.setTokenAdmin(null);
        return newUser;
    }
}
