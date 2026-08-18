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
import com.neohear.data.entity.Ear;
import com.neohear.data.entity.Mode;
import com.neohear.data.entity.TestResult;
import com.neohear.data.entity.TestSession;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class TestSessionDao_Impl implements TestSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TestSession> __insertionAdapterOfTestSession;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<TestSession> __deletionAdapterOfTestSession;

  private final EntityDeletionOrUpdateAdapter<TestSession> __updateAdapterOfTestSession;

  public TestSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTestSession = new EntityInsertionAdapter<TestSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `test_sessions` (`id`,`patientId`,`ear`,`stage`,`timestamp`,`preCheckNoiseLevel`,`preCheckSealOk`,`mode`,`rawSignalRef`,`snrValue`,`result`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestSession entity) {
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
        final String _tmp_2 = __converters.fromEar(entity.getEar());
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        statement.bindLong(4, entity.getStage());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindDouble(6, entity.getPreCheckNoiseLevel());
        final int _tmp_3 = entity.getPreCheckSealOk() ? 1 : 0;
        statement.bindLong(7, _tmp_3);
        final String _tmp_4 = __converters.fromMode(entity.getMode());
        if (_tmp_4 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_4);
        }
        if (entity.getRawSignalRef() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRawSignalRef());
        }
        if (entity.getSnrValue() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getSnrValue());
        }
        final String _tmp_5 = __converters.fromTestResult(entity.getResult());
        if (_tmp_5 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_5);
        }
      }
    };
    this.__deletionAdapterOfTestSession = new EntityDeletionOrUpdateAdapter<TestSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `test_sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestSession entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfTestSession = new EntityDeletionOrUpdateAdapter<TestSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `test_sessions` SET `id` = ?,`patientId` = ?,`ear` = ?,`stage` = ?,`timestamp` = ?,`preCheckNoiseLevel` = ?,`preCheckSealOk` = ?,`mode` = ?,`rawSignalRef` = ?,`snrValue` = ?,`result` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestSession entity) {
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
        final String _tmp_2 = __converters.fromEar(entity.getEar());
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        statement.bindLong(4, entity.getStage());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindDouble(6, entity.getPreCheckNoiseLevel());
        final int _tmp_3 = entity.getPreCheckSealOk() ? 1 : 0;
        statement.bindLong(7, _tmp_3);
        final String _tmp_4 = __converters.fromMode(entity.getMode());
        if (_tmp_4 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_4);
        }
        if (entity.getRawSignalRef() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getRawSignalRef());
        }
        if (entity.getSnrValue() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getSnrValue());
        }
        final String _tmp_5 = __converters.fromTestResult(entity.getResult());
        if (_tmp_5 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_5);
        }
        final String _tmp_6 = __converters.fromUuid(entity.getId());
        if (_tmp_6 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_6);
        }
      }
    };
  }

  @Override
  public Object insert(final TestSession session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestSession.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final TestSession session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTestSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TestSession session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTestSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final UUID id, final Continuation<? super TestSession> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestSession>() {
      @Override
      @Nullable
      public TestSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfEar = CursorUtil.getColumnIndexOrThrow(_cursor, "ear");
          final int _cursorIndexOfStage = CursorUtil.getColumnIndexOrThrow(_cursor, "stage");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPreCheckNoiseLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "preCheckNoiseLevel");
          final int _cursorIndexOfPreCheckSealOk = CursorUtil.getColumnIndexOrThrow(_cursor, "preCheckSealOk");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfRawSignalRef = CursorUtil.getColumnIndexOrThrow(_cursor, "rawSignalRef");
          final int _cursorIndexOfSnrValue = CursorUtil.getColumnIndexOrThrow(_cursor, "snrValue");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final TestSession _result;
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
            final Ear _tmpEar;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfEar)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfEar);
            }
            final Ear _tmp_6 = __converters.toEar(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Ear', but it was NULL.");
            } else {
              _tmpEar = _tmp_6;
            }
            final int _tmpStage;
            _tmpStage = _cursor.getInt(_cursorIndexOfStage);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpPreCheckNoiseLevel;
            _tmpPreCheckNoiseLevel = _cursor.getFloat(_cursorIndexOfPreCheckNoiseLevel);
            final boolean _tmpPreCheckSealOk;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPreCheckSealOk);
            _tmpPreCheckSealOk = _tmp_7 != 0;
            final Mode _tmpMode;
            final String _tmp_8;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmp_8 = null;
            } else {
              _tmp_8 = _cursor.getString(_cursorIndexOfMode);
            }
            final Mode _tmp_9 = __converters.toMode(_tmp_8);
            if (_tmp_9 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Mode', but it was NULL.");
            } else {
              _tmpMode = _tmp_9;
            }
            final String _tmpRawSignalRef;
            if (_cursor.isNull(_cursorIndexOfRawSignalRef)) {
              _tmpRawSignalRef = null;
            } else {
              _tmpRawSignalRef = _cursor.getString(_cursorIndexOfRawSignalRef);
            }
            final Float _tmpSnrValue;
            if (_cursor.isNull(_cursorIndexOfSnrValue)) {
              _tmpSnrValue = null;
            } else {
              _tmpSnrValue = _cursor.getFloat(_cursorIndexOfSnrValue);
            }
            final TestResult _tmpResult;
            final String _tmp_10;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmp_10 = null;
            } else {
              _tmp_10 = _cursor.getString(_cursorIndexOfResult);
            }
            final TestResult _tmp_11 = __converters.toTestResult(_tmp_10);
            if (_tmp_11 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.TestResult', but it was NULL.");
            } else {
              _tmpResult = _tmp_11;
            }
            _result = new TestSession(_tmpId,_tmpPatientId,_tmpEar,_tmpStage,_tmpTimestamp,_tmpPreCheckNoiseLevel,_tmpPreCheckSealOk,_tmpMode,_tmpRawSignalRef,_tmpSnrValue,_tmpResult);
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
  public Flow<List<TestSession>> getSessionsForPatient(final UUID patientId) {
    final String _sql = "SELECT * FROM test_sessions WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(patientId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<List<TestSession>>() {
      @Override
      @NonNull
      public List<TestSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfEar = CursorUtil.getColumnIndexOrThrow(_cursor, "ear");
          final int _cursorIndexOfStage = CursorUtil.getColumnIndexOrThrow(_cursor, "stage");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPreCheckNoiseLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "preCheckNoiseLevel");
          final int _cursorIndexOfPreCheckSealOk = CursorUtil.getColumnIndexOrThrow(_cursor, "preCheckSealOk");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfRawSignalRef = CursorUtil.getColumnIndexOrThrow(_cursor, "rawSignalRef");
          final int _cursorIndexOfSnrValue = CursorUtil.getColumnIndexOrThrow(_cursor, "snrValue");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final List<TestSession> _result = new ArrayList<TestSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSession _item;
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
            final Ear _tmpEar;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfEar)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfEar);
            }
            final Ear _tmp_6 = __converters.toEar(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Ear', but it was NULL.");
            } else {
              _tmpEar = _tmp_6;
            }
            final int _tmpStage;
            _tmpStage = _cursor.getInt(_cursorIndexOfStage);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpPreCheckNoiseLevel;
            _tmpPreCheckNoiseLevel = _cursor.getFloat(_cursorIndexOfPreCheckNoiseLevel);
            final boolean _tmpPreCheckSealOk;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPreCheckSealOk);
            _tmpPreCheckSealOk = _tmp_7 != 0;
            final Mode _tmpMode;
            final String _tmp_8;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmp_8 = null;
            } else {
              _tmp_8 = _cursor.getString(_cursorIndexOfMode);
            }
            final Mode _tmp_9 = __converters.toMode(_tmp_8);
            if (_tmp_9 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Mode', but it was NULL.");
            } else {
              _tmpMode = _tmp_9;
            }
            final String _tmpRawSignalRef;
            if (_cursor.isNull(_cursorIndexOfRawSignalRef)) {
              _tmpRawSignalRef = null;
            } else {
              _tmpRawSignalRef = _cursor.getString(_cursorIndexOfRawSignalRef);
            }
            final Float _tmpSnrValue;
            if (_cursor.isNull(_cursorIndexOfSnrValue)) {
              _tmpSnrValue = null;
            } else {
              _tmpSnrValue = _cursor.getFloat(_cursorIndexOfSnrValue);
            }
            final TestResult _tmpResult;
            final String _tmp_10;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmp_10 = null;
            } else {
              _tmp_10 = _cursor.getString(_cursorIndexOfResult);
            }
            final TestResult _tmp_11 = __converters.toTestResult(_tmp_10);
            if (_tmp_11 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.TestResult', but it was NULL.");
            } else {
              _tmpResult = _tmp_11;
            }
            _item = new TestSession(_tmpId,_tmpPatientId,_tmpEar,_tmpStage,_tmpTimestamp,_tmpPreCheckNoiseLevel,_tmpPreCheckSealOk,_tmpMode,_tmpRawSignalRef,_tmpSnrValue,_tmpResult);
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
