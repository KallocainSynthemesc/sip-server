package com.avinvivo.sip.server.dao;

import java.util.Collection;

/**
 * Définit les opérations de bases d'un crud pour l'accès en base de données
 * 
 * @author Cédric Chappert
 *
 * @param <T> le type de l'entité
 * @param <PK> le type de sa clé primaire
 */
public interface CRUDable<T, PK> {
	
	/**
	 * Réalise un insert SQL mais n'update pas (risque d'exception)
	 * @param obj
	 * @return
	 */
	public T save(T obj);	// CRUD Insert
	
	/**
	 * Realise un select * from entité where id = PK
	 * @param id
	 * @return
	 */
	public T get(PK id);       // CRUD GETOBJECT BY ID
	
	/**
	 * Réaliser un delete SQL
	 * @param id
	 * @return
	 */
	public T remove(PK id);
	
	/**
	 * réalise un select * from entite
	 * @return
	 */
	public Collection<T> select();
	
	/**
	 * realise un update, si l'entité n'est pas déjà présente, réalise un insert
	 * @param obj
	 */
	public void update(T obj);

}
