package com.neohear.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import com.neohear.data.converter.Converters;
import com.neohear.data.entity.Ear;
import com.neohear.data.entity.FollowUpEvent;
import com.neohear.data.entity.Mode;
import com.neohear.data.entity.Referral;
import com.neohear.data.entity.ReferralStatus;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DashboardDao_Impl implements DashboardDao {
  private final RoomDatabase __db;

  private final Converters __converters = new Converters();

  public DashboardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Object getDashboardCounts(final long start, final long end,
      final Continuation<? super DashboardCounts> $completion) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            (SELECT COUNT(*) FROM test_sessions WHERE timestamp BETWEEN ? AND ?) AS totalTests,\n"
            + "            (SELECT COUNT(*) FROM test_sessions WHERE result = 'PASS' AND timestamp BETWEEN ? AND ?) AS passCount,\n"
            + "            (SELECT COUNT(*) FROM test_sessions WHERE result = 'REFER' AND timestamp BETWEEN ? AND ?) AS referCount,\n"
            + "            (SELECT COUNT(*) FROM referrals WHERE status != 'COMPLETED') AS pendingReferrals,\n"
            + "            (SELECT COUNT(*) FROM referrals WHERE status = 'COMPLETED') AS resolvedReferrals\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    _argIndex = 3;
    _statement.bindLong(_argIndex, start);
    _argIndex = 4;
    _statement.bindLong(_argIndex, end);
    _argIndex = 5;
    _statement.bindLong(_argIndex, start);
    _argIndex = 6;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DashboardCounts>() {
      @Override
      @Nullable
      public DashboardCounts call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTotalTests = 0;
          final int _cursorIndexOfPassCount = 1;
          final int _cursorIndexOfReferCount = 2;
          final int _cursorIndexOfPendingReferrals = 3;
          final int _cursorIndexOfResolvedReferrals = 4;
          final DashboardCounts _result;
          if (_cursor.moveToFirst()) {
            final int _tmpTotalTests;
            _tmpTotalTests = _cursor.getInt(_cursorIndexOfTotalTests);
            final int _tmpPassCount;
            _tmpPassCount = _cursor.getInt(_cursorIndexOfPassCount);
            final int _tmpReferCount;
            _tmpReferCount = _cursor.getInt(_cursorIndexOfReferCount);
            final int _tmpPendingReferrals;
            _tmpPendingReferrals = _cursor.getInt(_cursorIndexOfPendingReferrals);
            final int _tmpResolvedReferrals;
            _tmpResolvedReferrals = _cursor.getInt(_cursorIndexOfResolvedReferrals);
            _result = new DashboardCounts(_tmpTotalTests,_tmpPassCount,_tmpReferCount,_tmpPendingReferrals,_tmpResolvedReferrals);
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
  public Flow<List<TestSession>> observeAllTestSessions() {
    final String _sql = "SELECT * FROM test_sessions ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
            final Ear _tmpEar;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfEar)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfEar);
            }
            final Ear _tmp_5 = __converters.toEar(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Ear', but it was NULL.");
            } else {
              _tmpEar = _tmp_5;
            }
            final int _tmpStage;
            _tmpStage = _cursor.getInt(_cursorIndexOfStage);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpPreCheckNoiseLevel;
            _tmpPreCheckNoiseLevel = _cursor.getFloat(_cursorIndexOfPreCheckNoiseLevel);
            final boolean _tmpPreCheckSealOk;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfPreCheckSealOk);
            _tmpPreCheckSealOk = _tmp_6 != 0;
            final Mode _tmpMode;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfMode);
            }
            final Mode _tmp_8 = __converters.toMode(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Mode', but it was NULL.");
            } else {
              _tmpMode = _tmp_8;
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
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfResult)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfResult);
            }
            final TestResult _tmp_10 = __converters.toTestResult(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.TestResult', but it was NULL.");
            } else {
              _tmpResult = _tmp_10;
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

  @Override
  public Flow<List<Referral>> observeAllReferrals() {
    final String _sql = "SELECT * FROM referrals ORDER BY createdAt ASC";
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
  public Flow<List<ModeCount>> observeModeCounts(final long start, final long end) {
    final String _sql = "\n"
            + "        SELECT mode, COUNT(*) AS count\n"
            + "        FROM test_sessions\n"
            + "        WHERE timestamp BETWEEN ? AND ?\n"
            + "        GROUP BY mode\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<List<ModeCount>>() {
      @Override
      @NonNull
      public List<ModeCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMode = 0;
          final int _cursorIndexOfCount = 1;
          final List<ModeCount> _result = new ArrayList<ModeCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModeCount _item;
            final Mode _tmpMode;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfMode);
            }
            final Mode _tmp_1 = __converters.toMode(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.Mode', but it was NULL.");
            } else {
              _tmpMode = _tmp_1;
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new ModeCount(_tmpMode,_tmpCount);
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
  public Flow<List<DailyTestCount>> observeDailyTestCounts(final long since) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            (timestamp / 86400000) * 86400000 AS dayStart,\n"
            + "            SUM(CASE WHEN result = 'PASS' THEN 1 ELSE 0 END) AS passCount,\n"
            + "            SUM(CASE WHEN result = 'REFER' THEN 1 ELSE 0 END) AS referCount\n"
            + "        FROM test_sessions\n"
            + "        WHERE timestamp >= ?\n"
            + "        GROUP BY dayStart\n"
            + "        ORDER BY dayStart ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<List<DailyTestCount>>() {
      @Override
      @NonNull
      public List<DailyTestCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDayStart = 0;
          final int _cursorIndexOfPassCount = 1;
          final int _cursorIndexOfReferCount = 2;
          final List<DailyTestCount> _result = new ArrayList<DailyTestCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTestCount _item;
            final long _tmpDayStart;
            _tmpDayStart = _cursor.getLong(_cursorIndexOfDayStart);
            final int _tmpPassCount;
            _tmpPassCount = _cursor.getInt(_cursorIndexOfPassCount);
            final int _tmpReferCount;
            _tmpReferCount = _cursor.getInt(_cursorIndexOfReferCount);
            _item = new DailyTestCount(_tmpDayStart,_tmpPassCount,_tmpReferCount);
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
