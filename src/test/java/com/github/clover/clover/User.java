package com.github.clover.clover;

import com.github.nkinsp.clover.annotation.Cascade;
import com.github.nkinsp.clover.annotation.Table;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.query.QueryWrapper;

import lombok.Data;

@Table
@Data
public class User {

	private Integer id;
	
	private String name;
	
	private String password;
	
	private String userName;
	
	
	public static void main(String[] args) {
		QueryWrapper<User> wrapper = new QueryWrapper<>(User.class);
		
		wrapper.eq("id", 100).and(w->w.eq("userName", "zhan")).orderBy("id").select(User.class).groupBy("id");
		
		String sql = wrapper.buildSql();
		
		
		System.out.println(sql);
		
	}
	
}
