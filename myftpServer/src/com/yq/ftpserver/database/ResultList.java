package com.yq.ftpserver.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ResultList implements List<Map<String,Object>>{
	
	List<Map<String,Object>> resultList;
	
	public ResultList(ResultSet rs)
	{
		try {
			ResultSetMetaData metadata = rs.getMetaData();
			resultList = new ArrayList<Map<String,Object>>();
			
			while(rs.next())
			{
				Map<String,Object> map = new HashMap<String,Object>();
				for(int i=1;i<=metadata.getColumnCount();i++)
					map.put(metadata.getColumnName(i), rs.getObject(i));
				resultList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean add(Map<String,Object> e) {

		return resultList.add(e);
	}

	@Override
	public void add(int index, Map<String,Object> element) {
		resultList.add(index,element);
	}

	@Override
	public boolean addAll(Collection c) {
		return resultList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return resultList.addAll(index, c);
	}

	@Override
	public void clear() {
		resultList.clear();		
	}

	@Override
	public boolean contains(Object o) {
		return resultList.contains(o);
	}

	@Override
	public boolean containsAll(Collection c) {
		return resultList.containsAll(c);
	}

	@Override
	public Map get(int index) {
		return resultList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return resultList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return resultList.isEmpty();
	}

	@Override
	public Iterator iterator() {
		return resultList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return resultList.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		return resultList.listIterator();
	}

	@Override
	public ListIterator listIterator(int index) {
		return resultList.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return resultList.remove(o);
	}

	@Override
	public Map remove(int index) {
		return resultList.remove(index);
	}

	@Override
	public boolean removeAll(Collection c) {
		return resultList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection c) {
		return resultList.retainAll(c);
	}

	@Override
	public  Map<String,Object> set(int index, Map element) {
		return resultList.set(index,element);
	}

	@Override
	public int size() {
		return resultList.size();
	}

	@Override
	public List<Map<String,Object>> subList(int fromIndex, int toIndex) {
		return resultList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return resultList.toArray();
	}

	@Override
	public Object[] toArray(Object[] a) {
		return resultList.toArray(a);
	}
	
}
