package com.neohear.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.neohear.data.entity.SyncRecord;
import com.neohear.data.entity.SyncState;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SyncDao_Impl implements SyncDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncRecord> __insertionAdapterOfSyncRecord;

  private final EntityDeletionOrUpdateAdapter<SyncRecord> __updateAdapterOfSyncRecord;

  public SyncDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncRecord = new EntityInsertionAdapter<SyncRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sync_records` (`id`,`entity_id`,`entity_type`,`is_demo`,`state`,`created_at`,`last_attempt_at`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEntityId());
        statement.bindString(3, entity.getEntityType());
        final int _tmp = entity.isDemo() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindString(5, __SyncState_enumToString(entity.getState()));
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getLastAttemptAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastAttemptAt());
        }
      }
    };
    this.__updateAdapterOfSyncRecord = new EntityDeletionOrUpdateAdapter<SyncRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sync_records` SET `id` = ?,`entity_id` = ?,`entity_type` = ?,`is_demo` = ?,`state` = ?,`created_at` = ?,`last_attempt_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEntityId());
        statement.bindString(3, entity.getEntityType());
        final int _tmp = entity.isDemo() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindString(5, __SyncState_enumToString(entity.getState()));
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getLastAttemptAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastAttemptAt());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SyncRecord record, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SyncRecord record, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSyncRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object countByState(final SyncState state,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_records WHERE state = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __SyncState_enumToString(state));
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countPending(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_records WHERE state = 'PENDING_SYNC'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPending(final Continuation<? super List<SyncRecord>> $completion) {
    final String _sql = "SELECT * FROM sync_records WHERE state = 'PENDING_SYNC'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncRecord>>() {
      @Override
      @NonNull
      public List<SyncRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entity_id");
          final int _cursorIndexOfEntityType = CursorUtil.getColumnIndexOrThrow(_cursor, "entity_type");
          final int _cursorIndexOfIsDemo = CursorUtil.getColumnIndexOrThrow(_cursor, "is_demo");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfLastAttemptAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_attempt_at");
          final List<SyncRecord> _result = new ArrayList<SyncRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final String _tmpEntityType;
            _tmpEntityType = _cursor.getString(_cursorIndexOfEntityType);
            final boolean _tmpIsDemo;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDemo);
            _tmpIsDemo = _tmp != 0;
            final SyncState _tmpState;
            _tmpState = __SyncState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpLastAttemptAt;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAt)) {
              _tmpLastAttemptAt = null;
            } else {
              _tmpLastAttemptAt = _cursor.getLong(_cursorIndexOfLastAttemptAt);
            }
            _item = new SyncRecord(_tmpId,_tmpEntityId,_tmpEntityType,_tmpIsDemo,_tmpState,_tmpCreatedAt,_tmpLastAttemptAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object totalCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object bulkUpdateState(final List<Long> ids, final SyncState state, final long attemptAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE sync_records SET state = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", last_attempt_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __SyncState_enumToString(state));
        _argIndex = 2;
        _stmt.bindLong(_argIndex, attemptAt);
        _argIndex = 3;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __SyncState_enumToString(@NonNull final SyncState _value) {
    switch (_value) {
      case LOCAL_ONLY: return "LOCAL_ONLY";
      case PENDING_SYNC: return "PENDING_SYNC";
      case SYNCED: return "SYNCED";
      case SYNC_FAILED: return "SYNC_FAILED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private SyncState __SyncState_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "LOCAL_ONLY": return SyncState.LOCAL_ONLY;
      case "PENDING_SYNC": return SyncState.PENDING_SYNC;
      case "SYNCED": return SyncState.SYNCED;
      case "SYNC_FAILED": return SyncState.SYNC_FAILED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
