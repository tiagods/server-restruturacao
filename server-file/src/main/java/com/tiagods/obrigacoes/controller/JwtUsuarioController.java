package com.tiagods.obrigacoes.controller;

import com.tiagods.obrigacoes.auth.JwtTokenUtil;
import com.tiagods.obrigacoes.auth.Token;
import com.tiagods.obrigacoes.dto.UsuarioDTO;
import com.tiagods.obrigacoes.exception.UsuarioNotFoundException;
import com.tiagods.obrigacoes.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class JwtUsuarioController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/autenticar")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UsuarioDTO usuario) throws Exception {
        authenticate(usuario.getEmail(), usuario.getSenha());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(usuario.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new Token(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> salvar(@RequestBody @Valid UsuarioDTO usuario,
                                    @RequestHeader("x-admin") String senhaAdmin) {
        try {
            UsuarioDTO usuarioDTO = userDetailsService.salvar(usuario, senhaAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\""+e.getMessage()+"\"}");
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
