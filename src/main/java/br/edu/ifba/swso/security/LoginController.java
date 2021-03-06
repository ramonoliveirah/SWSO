package br.edu.ifba.swso.security;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import br.edu.ifba.swso.business.Menu;
import br.edu.ifba.swso.business.Usuario;
import br.edu.ifba.swso.controller.BaseController;
import br.edu.ifba.swso.exception.BusinessException;
import br.edu.ifba.swso.exception.RequiredException;
import br.edu.ifba.swso.util.MensagemUtil;
import br.edu.ifba.swso.util.Util;

/**
 * Classe de controle de acesso
 * @author Ramon
 *
 */
@Named
@SessionScoped
public class LoginController extends BaseController implements Serializable {

	private static final long serialVersionUID = 7444956049460916877L;

	private Usuario usuario;

	private Credentials credentials;
		
	private MenuModel menuBarraHorizontal;
	
	private String linkMenu;
	
	private List<Menu> menuVerticalSistema;	
	
	public LoginController() {
		credentials = new Credentials();
		menuBarraHorizontal = new DefaultMenuModel();
	}

	/**
	 * Método autentica login e senha
	 * 
	 * @return
	 */
	public String autenticar() {

		try {				
			this.validarCampos();
			this.validarCredentials();
			montarMenu();						
			return redirectHome();
		}catch (RequiredException e) {
			//TODO TRATAR 
		}catch (BusinessException e) {
			//TODO TRATAR
		}catch (Exception e){
			//TODO TRATAR
		}
		return "login";
	}
	
	/**
	 * Valida obrigatoriedade dos campos informados pelo usuário
	 */
	public void validarCampos() throws RequiredException {
		if (Util.isNullOuVazio(credentials) || Util.isNullOuVazio(credentials.getLogin()) || Util.isNullOuVazio(credentials.getSenha())) {
			if (Util.isNullOuVazio(credentials.getLogin())) {
				facesMessager.error(MensagemUtil.obterMensagem("login.required.usuario"));
			} else if (Util.isNullOuVazio(credentials.getSenha())) {
				facesMessager.error(MensagemUtil.obterMensagem("login.required.senha"));
			}
			if (!Util.isNullOuVazio(credentials.getSenha())) {
				credentials.setSenha("");
			}
		}
	}
	
	public void validarCredentials() throws BusinessException {
		//AQUI DEVE SER CHECADO SE O USUÁRIO E SENHA INFORMADOS TÊM ACESSO AO SISTEMA
	}
	
	/**
	 * Método encerra a sessão e redireciona para página de login
	 * @return
	 */
	public String logout() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.resetBuffer();
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		limparCampos();
		return "/login.xhtml?faces-redirect=true";
	}

	/**
	 * Método limpa os campos informados
	 */
	private void limparCampos() {
		usuario = new Usuario();
		credentials = new Credentials();
	}
	
	/**
	 * Redireciona para página home.xhtml
	 * 
	 * @return
	 */
	public String redirectHome() {
		return "/home.xhtml";
	}
	
	/**
	 * Redireciona telas de menu
	 * @return
	 */
	public String redirecionarTela(){
		return getLinkMenu() + "?faces-redirect=true" ;
	}
	
	/**
	 * Monta o menu
	 * 
	 * @throws Exception
	 */	
	private void montarMenu() {

	}
	
	//MÉT0D0S DE ACESSO
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public MenuModel getMenuBarraHorizontal() {
		return menuBarraHorizontal;
	}

	public void setMenuBarraHorizontal(MenuModel menuBarraHorizontal) {
		this.menuBarraHorizontal = menuBarraHorizontal;
	}

	public String getLinkMenu() {
		return linkMenu;
	}

	public void setLinkMenu(String linkMenu) {
		this.linkMenu = linkMenu;
	}

	public List<Menu> getMenuVerticalSistema() {
		return menuVerticalSistema;
	}

	public void setMenuVerticalSistema(List<Menu> menuVerticalSistema) {
		this.menuVerticalSistema = menuVerticalSistema;
	}

}