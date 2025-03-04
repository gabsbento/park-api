package com.gabriel.demo_park_api.service;

import com.gabriel.demo_park_api.entity.Usuario;
import com.gabriel.demo_park_api.exception.EntityNotFoundException;
import com.gabriel.demo_park_api.exception.PasswordInvalidException;
import com.gabriel.demo_park_api.exception.UsernameUniqueViolationException;
import com.gabriel.demo_park_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario salvar(Usuario usuario){
        try{
            return usuarioRepository.save(usuario);
        }catch(org.springframework.dao.DataIntegrityViolationException ex){
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", usuario.getUsername()));
        }

    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario id=%s não encontrado", id))
        );
    }
    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if(!novaSenha.equals(confirmaSenha)){
            System.out.println("Senha não confere");
            throw new PasswordInvalidException("Nova senha não confere com confirmação de senha");
        }
        Usuario usuario = buscarPorId(id);
        if(!usuario.getPassword().equals(senhaAtual)){
            System.out.println("Senha atual errada");
            throw new PasswordInvalidException("Sua senha não confere");
        }
        usuario.setPassword(novaSenha);
        return usuario;
    }
    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario '%s' não encontrado", username))
        );
    }
    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
