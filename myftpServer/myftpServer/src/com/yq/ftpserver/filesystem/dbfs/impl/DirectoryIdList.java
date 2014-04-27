package com.yq.ftpserver.filesystem.dbfs.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.ftpserver.ftplet.User;

public class DirectoryIdList implements List<Object>{
	
	private User user;
	private List<Integer> idList;
	
	public DirectoryIdList(User u,String path)
	{
		user = u;
		idList = new ArrayList<Integer>();
	}
	
	@Override
	public boolean add(Object e) {

		return idList.add((Integer) e);
	}

	@Override
	public void add(int index, Object element) {
		idList.add(index,(Integer) element);
	}

	@Override
	public boolean addAll(Collection c) {
		return idList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return idList.addAll(index, c);
	}

	@Override
	public void clear() {
		idList.clear();		
	}

	@Override
	public boolean contains(Object o) {
		return idList.contains(o);
	}

	@Override
	public boolean containsAll(Collection c) {
		return idList.containsAll(c);
	}

	@Override
	public Object get(int index) {
		return idList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return idList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return idList.isEmpty();
	}

	@Override
	public Iterator iterator() {
		return idList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return idList.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		return idList.listIterator();
	}

	@Override
	public ListIterator listIterator(int index) {
		return idList.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return idList.remove(o);
	}

	@Override
	public Object remove(int index) {
		return idList.remove(index);
	}

	@Override
	public boolean removeAll(Collection c) {
		return idList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection c) {
		return idList.retainAll(c);
	}

	@Override
	public Object set(int index, Object element) {
		return idList.set(index, (Integer) element);
	}

	@Override
	public int size() {
		return idList.size();
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		return idList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return idList.toArray();
	}

	@Override
	public Object[] toArray(Object[] a) {
		return idList.toArray(a);
	}
	
}
