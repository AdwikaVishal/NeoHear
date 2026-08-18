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
import com.neohear.data.entity.Patient;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PatientDao_Impl implements PatientDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Patient> __insertionAdapterOfPatient;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Patient> __deletionAdapterOfPatient;

  private final EntityDeletionOrUpdateAdapter<Patient> __updateAdapterOfPatient;

  public PatientDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPatient = new EntityInsertionAdapter<Patient>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `patients` (`id`,`displayNameOrCode`,`dob`,`sex`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Patient entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindString(2, entity.getDisplayNameOrCode());
        final Long _tmp_1 = __converters.fromDate(entity.getDob());
        if (_tmp_1 == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp_1);
        }
        if (entity.getSex() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSex());
        }
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfPatient = new EntityDeletionOrUpdateAdapter<Patient>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `patients` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Patient entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfPatient = new EntityDeletionOrUpdateAdapter<Patient>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `patients` SET `id` = ?,`displayNameOrCode` = ?,`dob` = ?,`sex` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Patient entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindString(2, entity.getDisplayNameOrCode());
        final Long _tmp_1 = __converters.fromDate(entity.getDob());
        if (_tmp_1 == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp_1);
        }
        if (entity.getSex() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSex());
        }
        statement.bindLong(5, entity.getCreatedAt());
        final String _tmp_2 = __converters.fromUuid(entity.getId());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
      }
    };
  }

  @Override
  public Object insert(final Patient patient, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPatient.insert(patient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Patient patient, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPatient.handle(patient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Patient patient, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPatient.handle(patient);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final UUID id, final Continuation<? super Patient> $completion) {
    final String _sql = "SELECT * FROM patients WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Patient>() {
      @Override
      @Nullable
      public Patient call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayNameOrCode = CursorUtil.getColumnIndexOrThrow(_cursor, "displayNameOrCode");
          final int _cursorIndexOfDob = CursorUtil.getColumnIndexOrThrow(_cursor, "dob");
          final int _cursorIndexOfSex = CursorUtil.getColumnIndexOrThrow(_cursor, "sex");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Patient _result;
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
            final String _tmpDisplayNameOrCode;
            _tmpDisplayNameOrCode = _cursor.getString(_cursorIndexOfDisplayNameOrCode);
            final Date _tmpDob;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfDob)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfDob);
            }
            final Date _tmp_4 = __converters.toDate(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpDob = _tmp_4;
            }
            final String _tmpSex;
            if (_cursor.isNull(_cursorIndexOfSex)) {
              _tmpSex = null;
            } else {
              _tmpSex = _cursor.getString(_cursorIndexOfSex);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Patient(_tmpId,_tmpDisplayNameOrCode,_tmpDob,_tmpSex,_tmpCreatedAt);
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
  public Flow<List<Patient>> getAllPatients() {
    final String _sql = "SELECT * FROM patients ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"patients"}, new Callable<List<Patient>>() {
      @Override
      @NonNull
      public List<Patient> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayNameOrCode = CursorUtil.getColumnIndexOrThrow(_cursor, "displayNameOrCode");
          final int _cursorIndexOfDob = CursorUtil.getColumnIndexOrThrow(_cursor, "dob");
          final int _cursorIndexOfSex = CursorUtil.getColumnIndexOrThrow(_cursor, "sex");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Patient> _result = new ArrayList<Patient>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Patient _item;
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
            final String _tmpDisplayNameOrCode;
            _tmpDisplayNameOrCode = _cursor.getString(_cursorIndexOfDisplayNameOrCode);
            final Date _tmpDob;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfDob)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfDob);
            }
            final Date _tmp_3 = __converters.toDate(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpDob = _tmp_3;
            }
            final String _tmpSex;
            if (_cursor.isNull(_cursorIndexOfSex)) {
              _tmpSex = null;
            } else {
              _tmpSex = _cursor.getString(_cursorIndexOfSex);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Patient(_tmpId,_tmpDisplayNameOrCode,_tmpDob,_tmpSex,_tmpCreatedAt);
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
