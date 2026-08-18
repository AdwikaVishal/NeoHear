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
import com.neohear.data.entity.RiskLevel;
import com.neohear.data.entity.RiskQuestionnaireResponse;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RiskQuestionnaireResponseDao_Impl implements RiskQuestionnaireResponseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RiskQuestionnaireResponse> __insertionAdapterOfRiskQuestionnaireResponse;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<RiskQuestionnaireResponse> __deletionAdapterOfRiskQuestionnaireResponse;

  private final EntityDeletionOrUpdateAdapter<RiskQuestionnaireResponse> __updateAdapterOfRiskQuestionnaireResponse;

  public RiskQuestionnaireResponseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRiskQuestionnaireResponse = new EntityInsertionAdapter<RiskQuestionnaireResponse>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `risk_questionnaire_responses` (`id`,`patientId`,`timestamp`,`answers`,`riskLevel`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiskQuestionnaireResponse entity) {
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
        final String _tmp_2 = __converters.fromStringMap(entity.getAnswers());
        if (_tmp_2 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_2);
        }
        final String _tmp_3 = __converters.fromRiskLevel(entity.getRiskLevel());
        if (_tmp_3 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfRiskQuestionnaireResponse = new EntityDeletionOrUpdateAdapter<RiskQuestionnaireResponse>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `risk_questionnaire_responses` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiskQuestionnaireResponse entity) {
        final String _tmp = __converters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfRiskQuestionnaireResponse = new EntityDeletionOrUpdateAdapter<RiskQuestionnaireResponse>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `risk_questionnaire_responses` SET `id` = ?,`patientId` = ?,`timestamp` = ?,`answers` = ?,`riskLevel` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiskQuestionnaireResponse entity) {
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
        final String _tmp_2 = __converters.fromStringMap(entity.getAnswers());
        if (_tmp_2 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_2);
        }
        final String _tmp_3 = __converters.fromRiskLevel(entity.getRiskLevel());
        if (_tmp_3 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_3);
        }
        final String _tmp_4 = __converters.fromUuid(entity.getId());
        if (_tmp_4 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_4);
        }
      }
    };
  }

  @Override
  public Object insert(final RiskQuestionnaireResponse response,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRiskQuestionnaireResponse.insert(response);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RiskQuestionnaireResponse response,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRiskQuestionnaireResponse.handle(response);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RiskQuestionnaireResponse response,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRiskQuestionnaireResponse.handle(response);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final UUID id,
      final Continuation<? super RiskQuestionnaireResponse> $completion) {
    final String _sql = "SELECT * FROM risk_questionnaire_responses WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RiskQuestionnaireResponse>() {
      @Override
      @Nullable
      public RiskQuestionnaireResponse call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "answers");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final RiskQuestionnaireResponse _result;
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
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final Map<String, String> _tmpAnswers;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfAnswers)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfAnswers);
            }
            final Map<String, String> _tmp_6 = __converters.toStringMap(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Map<java.lang.String, java.lang.String>', but it was NULL.");
            } else {
              _tmpAnswers = _tmp_6;
            }
            final RiskLevel _tmpRiskLevel;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfRiskLevel)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfRiskLevel);
            }
            final RiskLevel _tmp_8 = __converters.toRiskLevel(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.RiskLevel', but it was NULL.");
            } else {
              _tmpRiskLevel = _tmp_8;
            }
            _result = new RiskQuestionnaireResponse(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpAnswers,_tmpRiskLevel);
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
  public Flow<List<RiskQuestionnaireResponse>> getResponsesForPatient(final UUID patientId) {
    final String _sql = "SELECT * FROM risk_questionnaire_responses WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromUuid(patientId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"risk_questionnaire_responses"}, new Callable<List<RiskQuestionnaireResponse>>() {
      @Override
      @NonNull
      public List<RiskQuestionnaireResponse> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "answers");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final List<RiskQuestionnaireResponse> _result = new ArrayList<RiskQuestionnaireResponse>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiskQuestionnaireResponse _item;
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
            final Map<String, String> _tmpAnswers;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfAnswers)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfAnswers);
            }
            final Map<String, String> _tmp_6 = __converters.toStringMap(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Map<java.lang.String, java.lang.String>', but it was NULL.");
            } else {
              _tmpAnswers = _tmp_6;
            }
            final RiskLevel _tmpRiskLevel;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfRiskLevel)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfRiskLevel);
            }
            final RiskLevel _tmp_8 = __converters.toRiskLevel(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.neohear.data.entity.RiskLevel', but it was NULL.");
            } else {
              _tmpRiskLevel = _tmp_8;
            }
            _item = new RiskQuestionnaireResponse(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpAnswers,_tmpRiskLevel);
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
