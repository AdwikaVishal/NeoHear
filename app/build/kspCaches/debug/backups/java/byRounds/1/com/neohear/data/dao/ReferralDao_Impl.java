package com.neohear.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.neohear.data.converter.Converters;
import com.neohear.data.entity.FollowUpEvent;
import com.neohear.data.entity.Referral;
import com.neohear.data.entity.ReferralStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReferralDao_Impl implements ReferralDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Referral> __insertionAdapterOfReferral;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Referral> __deletionAdapterOfReferral;

  private final EntityDeletionOrUpdateAdapter<Referral> __updateAdapterOfReferral;

  public ReferralDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReferral = new EntityInsertionAdapter<Referral>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `referrals` (`id`,`patientId`,`testSessionId`,`status`,`createdAt`,`updatedAt`,`followUpLog`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Referral entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = __converters.fromUuid(entity.getPatientId());
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        final String _tmp_2 = __converters.fromUuid(entity.getTestSessionId());
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        final String _tmp_3 = __converters.fromReferralStatus(entity.getStatus());
        if (_tmp_3 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_3);
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
        final String _tmp_4 = __converters.fromFollowUpEvents(entity.getFollowUpLog());
        if (_tmp_4 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_4);
        }
      }
    };
    this.__deletionAdapterOfReferral = new EntityDeletionOrUpdateAdapter<Referral>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `referrals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Referral entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfReferral = new EntityDeletionOrUpdateAdapter<Referral>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `referrals` SET `id` = ?,`patientId` = ?,`testSessionId` = ?,`status` = ?,`createdAt` = ?,`updatedAt` = ?,`followUpLog` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Referral entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = __converters.fromUuid(entity.getPatientId());
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        final String _tmp_2 = __converters.fromUuid(entity.getTestSessionId());
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        final String _tmp_3 = __converters.fromReferralStatus(entity.getStatus());
        if (_tmp_3 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_3);
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
        final String _tmp_4 = __converters.fromFollowUpEvents(entity.getFollowUpLog());
        if (_tmp_4 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_4);
        }
        final String _tmp_5 = __converters.fromUuid(entity.getId());
        if (_tmp_5 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_5);
        }
      }
    };
  }

  @Override
  public Object insert(final Referral referral, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReferral.insert(referral);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Referral referral, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReferral.handle(referral);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Referral referral, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReferral.handle(referral);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final UUID id, final Continuation<? super Referral> $completion) {
    final String _sql = "SELECT * FROM referrals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Referral>() {
      @Override
      @Nullable
      public Referral call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTestSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "testSessionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfFollowUpLog = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpLog");
          final Referral _result;
          if (_cursor.moveToFirst()) {
            final UUID _tmpId;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_2 = __converters.toUuid(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_2;
            }
            final UUID _tmpPatientId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPatientId);
            }
            final UUID _tmp_4 = __converters.toUuid(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpPatientId = _tmp_4;
            }
            final UUID _tmpTestSessionId;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfTestSessionId)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfTestSessionId);
            }
            final UUID _tmp_6 = __converters.toUuid(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpTestSessionId = _tmp_6;
            }
            final ReferralStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final ReferralStatus _tmp_8 = __converters.toReferralStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.ReferralStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final List<FollowUpEvent> _tmpFollowUpLog;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfFollowUpLog)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfFollowUpLog);
            }
            final List<FollowUpEvent> _tmp_10 = __converters.toFollowUpEvents(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.neohear.data.entity.FollowUpEvent>', but it was NULL.");
            } else {
              _tmpFollowUpLog = _tmp_10;
            }
            _result = new Referral(_tmpId,_tmpPatientId,_tmpTestSessionId,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpFollowUpLog);
          } else {
            _result = null;
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
  public Flow<List<Referral>> getIncompleteReferrals() {
    final String _sql = "SELECT * FROM referrals WHERE status != 'COMPLETED' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"referrals"}, new Callable<List<Referral>>() {
      @Override
      @NonNull
      public List<Referral> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTestSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "testSessionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfFollowUpLog = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpLog");
          final List<Referral> _result = new ArrayList<Referral>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Referral _item;
            final UUID _tmpId;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_1 = __converters.toUuid(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_1;
            }
            final UUID _tmpPatientId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPatientId);
            }
            final UUID _tmp_3 = __converters.toUuid(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpPatientId = _tmp_3;
            }
            final UUID _tmpTestSessionId;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTestSessionId)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfTestSessionId);
            }
            final UUID _tmp_5 = __converters.toUuid(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpTestSessionId = _tmp_5;
            }
            final ReferralStatus _tmpStatus;
            final String _tmp_6;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getString(_cursorIndexOfStatus);
            }
            final ReferralStatus _tmp_7 = __converters.toReferralStatus(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.ReferralStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_7;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final List<FollowUpEvent> _tmpFollowUpLog;
            final String _tmp_8;
            if (_cursor.isNull(_cursorIndexOfFollowUpLog)) {
              _tmp_8 = null;
            } else {
              _tmp_8 = _cursor.getString(_cursorIndexOfFollowUpLog);
            }
            final List<FollowUpEvent> _tmp_9 = __converters.toFollowUpEvents(_tmp_8);
            if (_tmp_9 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.neohear.data.entity.FollowUpEvent>', but it was NULL.");
            } else {
              _tmpFollowUpLog = _tmp_9;
            }
            _item = new Referral(_tmpId,_tmpPatientId,_tmpTestSessionId,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpFollowUpLog);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Referral>> getAllReferrals() {
    final String _sql = "SELECT * FROM referrals ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"referrals"}, new Callable<List<Referral>>() {
      @Override
      @NonNull
      public List<Referral> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTestSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "testSessionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfFollowUpLog = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpLog");
          final List<Referral> _result = new ArrayList<Referral>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Referral _item;
            final UUID _tmpId;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_1 = __converters.toUuid(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_1;
            }
            final UUID _tmpPatientId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPatientId);
            }
            final UUID _tmp_3 = __converters.toUuid(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpPatientId = _tmp_3;
            }
            final UUID _tmpTestSessionId;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTestSessionId)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfTestSessionId);
            }
            final UUID _tmp_5 = __converters.toUuid(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpTestSessionId = _tmp_5;
            }
            final ReferralStatus _tmpStatus;
            final String _tmp_6;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getString(_cursorIndexOfStatus);
            }
            final ReferralStatus _tmp_7 = __converters.toReferralStatus(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.ReferralStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_7;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final List<FollowUpEvent> _tmpFollowUpLog;
            final String _tmp_8;
            if (_cursor.isNull(_cursorIndexOfFollowUpLog)) {
              _tmp_8 = null;
            } else {
              _tmp_8 = _cursor.getString(_cursorIndexOfFollowUpLog);
            }
            final List<FollowUpEvent> _tmp_9 = __converters.toFollowUpEvents(_tmp_8);
            if (_tmp_9 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.neohear.data.entity.FollowUpEvent>', but it was NULL.");
            } else {
              _tmpFollowUpLog = _tmp_9;
            }
            _item = new Referral(_tmpId,_tmpPatientId,_tmpTestSessionId,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpFollowUpLog);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingReferralsOlderThan(final long cutoffTimestamp,
      final Continuation<? super List<Referral>> $completion) {
    final String _sql = "SELECT * FROM referrals WHERE status = 'PENDING' AND createdAt <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cutoffTimestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Referral>>() {
      @Override
      @NonNull
      public List<Referral> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTestSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "testSessionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfFollowUpLog = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpLog");
          final List<Referral> _result = new ArrayList<Referral>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Referral _item;
            final UUID _tmpId;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_1 = __converters.toUuid(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_1;
            }
            final UUID _tmpPatientId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPatientId);
            }
            final UUID _tmp_3 = __converters.toUuid(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpPatientId = _tmp_3;
            }
            final UUID _tmpTestSessionId;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTestSessionId)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfTestSessionId);
            }
            final UUID _tmp_5 = __converters.toUuid(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpTestSessionId = _tmp_5;
            }
            final ReferralStatus _tmpStatus;
            final String _tmp_6;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getString(_cursorIndexOfStatus);
            }
            final ReferralStatus _tmp_7 = __converters.toReferralStatus(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.ReferralStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_7;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final List<FollowUpEvent> _tmpFollowUpLog;
            final String _tmp_8;
            if (_cursor.isNull(_cursorIndexOfFollowUpLog)) {
              _tmp_8 = null;
            } else {
              _tmp_8 = _cursor.getString(_cursorIndexOfFollowUpLog);
            }
            final List<FollowUpEvent> _tmp_9 = __converters.toFollowUpEvents(_tmp_8);
            if (_tmp_9 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.neohear.data.entity.FollowUpEvent>', but it was NULL.");
            } else {
              _tmpFollowUpLog = _tmp_9;
            }
            _item = new Referral(_tmpId,_tmpPatientId,_tmpTestSessionId,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpFollowUpLog);
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
  public Flow<List<Referral>> getReferralsForPatient(final UUID patientId) {
    final String _sql = "SELECT * FROM referrals WHERE patientId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(patientId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"referrals"}, new Callable<List<Referral>>() {
      @Override
      @NonNull
      public List<Referral> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTestSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "testSessionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfFollowUpLog = CursorUtil.getColumnIndexOrThrow(_cursor, "followUpLog");
          final List<Referral> _result = new ArrayList<Referral>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Referral _item;
            final UUID _tmpId;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_2 = __converters.toUuid(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_2;
            }
            final UUID _tmpPatientId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPatientId);
            }
            final UUID _tmp_4 = __converters.toUuid(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpPatientId = _tmp_4;
            }
            final UUID _tmpTestSessionId;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfTestSessionId)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfTestSessionId);
            }
            final UUID _tmp_6 = __converters.toUuid(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpTestSessionId = _tmp_6;
            }
            final ReferralStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final ReferralStatus _tmp_8 = __converters.toReferralStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.ReferralStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final List<FollowUpEvent> _tmpFollowUpLog;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfFollowUpLog)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfFollowUpLog);
            }
            final List<FollowUpEvent> _tmp_10 = __converters.toFollowUpEvents(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.neohear.data.entity.FollowUpEvent>', but it was NULL.");
            } else {
              _tmpFollowUpLog = _tmp_10;
            }
            _item = new Referral(_tmpId,_tmpPatientId,_tmpTestSessionId,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpFollowUpLog);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
