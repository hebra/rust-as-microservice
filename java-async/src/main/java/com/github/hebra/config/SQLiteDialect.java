package com.github.hebra.config;

import org.springframework.data.relational.core.dialect.AnsiDialect;
import org.springframework.data.relational.core.dialect.ArrayColumns;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.LockOptions;

public class SQLiteDialect extends AnsiDialect {

    public static final SQLiteDialect INSTANCE = new SQLiteDialect();

    @Override
    public IdentifierProcessing getIdentifierProcessing() {
        return IdentifierProcessing.NONE; // Informix doesn't like double quotes around identifiers
    }

    @Override
    public LockClause lock() {
        return LOCK_CLAUSE;
    }

    @Override
    public ArrayColumns getArraySupport() {
        return ArrayColumns.Unsupported.INSTANCE;
    }

    @Override
    public LimitClause limit() {
        return LIMIT_CLAUSE;
    }

    private static final LockClause LOCK_CLAUSE = new LockClause() {

        @Override
        public String getLock(LockOptions lockOptions) {
            return "FOR UPDATE";
        }

        @Override
        public Position getClausePosition() {
            return Position.AFTER_ORDER_BY;
        }
    };

    private static final LimitClause LIMIT_CLAUSE = new LimitClause() {

        @Override
        public String getLimit(long limit) {
            return String.format("FIRST %d", limit);
        }

        @Override
        public String getOffset(long offset) {
            return String.format("SKIP %d", offset);
        }

        @Override
        public String getLimitOffset(long limit, long offset) {
            return String.format("SKIP %d FIRST %d", offset, limit);
        }

        @Override
        public Position getClausePosition() {
            return Position.AFTER_ORDER_BY;
        }
    };
}

