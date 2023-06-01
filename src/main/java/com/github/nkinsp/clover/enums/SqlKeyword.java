package com.github.nkinsp.clover.enums;


public enum SqlKeyword {

	
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("%s IN ( %s )"),
    NOT_IN("%s NOT IN ( %s )"),
    LIKE("%s LIKE ?"),
    NOT_LIKE("NOT LIKE"),
    EQ("%s = ?"),
    NE("%s <> ?"),
    GT("%s > ?"),
    GE("%s >= ?"),
    LT("%s < ?"),
    LE("%s <= ?"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY %s"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY %s"),
    EXISTS("EXISTS (%s)"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("%s BETWEEN ? AND ?"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC"),
	WHERE("WHERE"),
	INSERT_SQL("INSERT INTO %s (%s) VALUES (%s)"),
	UPDATE_SQL("UPDATE %s SET %s"),
	DELETE_SQL("DELETE FROM %s"),
	SELECT_SQL("SELECT %s FROM %s"),
	COUNT("COUNT(%s)")
	;

	
    public String value;
    
    public String format(Object...values) {
    	return String.format(this.value, values);
    }
    
    
    private SqlKeyword(String value) {
    	
    	this.value = value;
    }
 
	
}
