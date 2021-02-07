package org.saphka.locationtracker.dao;

import org.jooq.DSLContext;
import org.saphka.locationtracker.dao.jooq.Tables;
import org.saphka.locationtracker.dao.jooq.tables.records.UsersRecord;
import org.saphka.locationtracker.dao.util.AsyncHelper;
import org.saphka.locationtracker.exception.ErrorCodeException;
import org.saphka.locationtracker.exception.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dslContext;
    private final AsyncHelper async;
    private final MessageSource messageSource;

    @Autowired
    public UserRepositoryImpl(DSLContext dslContext, AsyncHelper async, MessageSource messageSource) {
        this.dslContext = dslContext;
        this.async = async;
        this.messageSource = messageSource;
    }

    @Override
    public Mono<UsersRecord> getUserByIAlias(String alias) {
        return async.from(() -> dslContext.selectFrom(Tables.USERS)
                .where(Tables.USERS.USER_ALIAS.eq(alias))
                .fetchOptional()
                .orElseThrow(() -> new ErrorCodeException(
                        ErrorHandler.USER_NOT_FOUND,
                        messageSource.getMessage(
                                "user.not.found",
                                new Object[]{alias},
                                LocaleContextHolder.getLocale())
                )));
    }
}