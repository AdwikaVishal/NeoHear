package com.neohear.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.neohear.data.converter.Converters;
import com.neohear.data.entity.CryAnalysis;
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
public final class CryAnalysisDao_Impl implements CryAnalysisDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CryAnalysis> __insertionAdapterOfCryAnalysis;

  private final Converters __converters = new Converters();

  public CryAnalysisDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCryAnalysis = new EntityInsertionAdapter<CryAnalysis>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cry_analyses` (`id`,`patientId`,`timestamp`,`avgPitchHz`,`pitchStdDev`,`avgEnergyDb`,`jitter`,`shimmer`,`voicingRatio`,`riskFlags`,`isExperimental`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CryAnalysis entity) {
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
        statement.bindLong(3, entity.getTimestamp());
        statement.bindDouble(4, entity.getAvgPitchHz());
        statement.bindDouble(5, entity.getPitchStdDev());
        statement.bindDouble(6, entity.getAvgEnergyDb());
        statement.bindDouble(7, entity.getJitter());
        statement.bindDouble(8, entity.getShimmer());
        statement.bindDouble(9, entity.getVoicingRatio());
        statement.bindLong(10, entity.getRiskFlags());
        final int _tmp_2 = entity.isExperimental() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
      }
    };
  }

  @Override
  public Object insert(final CryAnalysis analysis, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCryAnalysis.insert(analysis);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CryAnalysis>> getByPatient(final UUID patientId) {
    final String _sql = "SELECT * FROM cry_analyses WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(patientId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cry_analyses"}, new Callable<List<CryAnalysis>>() {
      @Override
      @NonNull
      public List<CryAnalysis> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAvgPitchHz = CursorUtil.getColumnIndexOrThrow(_cursor, "avgPitchHz");
          final int _cursorIndexOfPitchStdDev = CursorUtil.getColumnIndexOrThrow(_cursor, "pitchStdDev");
          final int _cursorIndexOfAvgEnergyDb = CursorUtil.getColumnIndexOrThrow(_cursor, "avgEnergyDb");
          final int _cursorIndexOfJitter = CursorUtil.getColumnIndexOrThrow(_cursor, "jitter");
          final int _cursorIndexOfShimmer = CursorUtil.getColumnIndexOrThrow(_cursor, "shimmer");
          final int _cursorIndexOfVoicingRatio = CursorUtil.getColumnIndexOrThrow(_cursor, "voicingRatio");
          final int _cursorIndexOfRiskFlags = CursorUtil.getColumnIndexOrThrow(_cursor, "riskFlags");
          final int _cursorIndexOfIsExperimental = CursorUtil.getColumnIndexOrThrow(_cursor, "isExperimental");
          final List<CryAnalysis> _result = new ArrayList<CryAnalysis>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CryAnalysis _item;
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
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpAvgPitchHz;
            _tmpAvgPitchHz = _cursor.getFloat(_cursorIndexOfAvgPitchHz);
            final float _tmpPitchStdDev;
            _tmpPitchStdDev = _cursor.getFloat(_cursorIndexOfPitchStdDev);
            final float _tmpAvgEnergyDb;
            _tmpAvgEnergyDb = _cursor.getFloat(_cursorIndexOfAvgEnergyDb);
            final float _tmpJitter;
            _tmpJitter = _cursor.getFloat(_cursorIndexOfJitter);
            final float _tmpShimmer;
            _tmpShimmer = _cursor.getFloat(_cursorIndexOfShimmer);
            final float _tmpVoicingRatio;
            _tmpVoicingRatio = _cursor.getFloat(_cursorIndexOfVoicingRatio);
            final int _tmpRiskFlags;
            _tmpRiskFlags = _cursor.getInt(_cursorIndexOfRiskFlags);
            final boolean _tmpIsExperimental;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExperimental);
            _tmpIsExperimental = _tmp_5 != 0;
            _item = new CryAnalysis(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpAvgPitchHz,_tmpPitchStdDev,_tmpAvgEnergyDb,_tmpJitter,_tmpShimmer,_tmpVoicingRatio,_tmpRiskFlags,_tmpIsExperimental);
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
