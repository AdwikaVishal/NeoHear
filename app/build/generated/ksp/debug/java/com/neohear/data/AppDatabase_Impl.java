package com.neohear.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.neohear.data.dao.CryAnalysisDao;
import com.neohear.data.dao.CryAnalysisDao_Impl;
import com.neohear.data.dao.DashboardDao;
import com.neohear.data.dao.DashboardDao_Impl;
import com.neohear.data.dao.PatientDao;
import com.neohear.data.dao.PatientDao_Impl;
import com.neohear.data.dao.ReferralDao;
import com.neohear.data.dao.ReferralDao_Impl;
import com.neohear.data.dao.RiskQuestionnaireResponseDao;
import com.neohear.data.dao.RiskQuestionnaireResponseDao_Impl;
import com.neohear.data.dao.SyncDao;
import com.neohear.data.dao.SyncDao_Impl;
import com.neohear.data.dao.TestSessionDao;
import com.neohear.data.dao.TestSessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PatientDao _patientDao;

  private volatile TestSessionDao _testSessionDao;

  private volatile ReferralDao _referralDao;

  private volatile RiskQuestionnaireResponseDao _riskQuestionnaireResponseDao;

  private volatile DashboardDao _dashboardDao;

  private volatile SyncDao _syncDao;

  private volatile CryAnalysisDao _cryAnalysisDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `patients` (`id` TEXT NOT NULL, `displayNameOrCode` TEXT NOT NULL, `dob` INTEGER NOT NULL, `sex` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_sessions` (`id` TEXT NOT NULL, `patientId` TEXT NOT NULL, `ear` TEXT NOT NULL, `stage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `preCheckNoiseLevel` REAL NOT NULL, `preCheckSealOk` INTEGER NOT NULL, `mode` TEXT NOT NULL, `rawSignalRef` TEXT, `snrValue` REAL, `result` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`patientId`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_patientId` ON `test_sessions` (`patientId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `referrals` (`id` TEXT NOT NULL, `patientId` TEXT NOT NULL, `testSessionId` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `followUpLog` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`patientId`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`testSessionId`) REFERENCES `test_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_referrals_patientId` ON `referrals` (`patientId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_referrals_testSessionId` ON `referrals` (`testSessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `risk_questionnaire_responses` (`id` TEXT NOT NULL, `patientId` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `answers` TEXT NOT NULL, `riskLevel` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`patientId`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_risk_questionnaire_responses_patientId` ON `risk_questionnaire_responses` (`patientId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entity_id` TEXT NOT NULL, `entity_type` TEXT NOT NULL, `is_demo` INTEGER NOT NULL, `state` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `last_attempt_at` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cry_analyses` (`id` TEXT NOT NULL, `patientId` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `avgPitchHz` REAL NOT NULL, `pitchStdDev` REAL NOT NULL, `avgEnergyDb` REAL NOT NULL, `jitter` REAL NOT NULL, `shimmer` REAL NOT NULL, `voicingRatio` REAL NOT NULL, `riskFlags` INTEGER NOT NULL, `isExperimental` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`patientId`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_cry_analyses_patientId` ON `cry_analyses` (`patientId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4851b94555f699769ab17a33b5de0151')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `patients`");
        db.execSQL("DROP TABLE IF EXISTS `test_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `referrals`");
        db.execSQL("DROP TABLE IF EXISTS `risk_questionnaire_responses`");
        db.execSQL("DROP TABLE IF EXISTS `sync_records`");
        db.execSQL("DROP TABLE IF EXISTS `cry_analyses`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPatients = new HashMap<String, TableInfo.Column>(5);
        _columnsPatients.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatients.put("displayNameOrCode", new TableInfo.Column("displayNameOrCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatients.put("dob", new TableInfo.Column("dob", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatients.put("sex", new TableInfo.Column("sex", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatients.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPatients = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPatients = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPatients = new TableInfo("patients", _columnsPatients, _foreignKeysPatients, _indicesPatients);
        final TableInfo _existingPatients = TableInfo.read(db, "patients");
        if (!_infoPatients.equals(_existingPatients)) {
          return new RoomOpenHelper.ValidationResult(false, "patients(com.neohear.data.entity.Patient).\n"
                  + " Expected:\n" + _infoPatients + "\n"
                  + " Found:\n" + _existingPatients);
        }
        final HashMap<String, TableInfo.Column> _columnsTestSessions = new HashMap<String, TableInfo.Column>(11);
        _columnsTestSessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("patientId", new TableInfo.Column("patientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("ear", new TableInfo.Column("ear", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("stage", new TableInfo.Column("stage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("preCheckNoiseLevel", new TableInfo.Column("preCheckNoiseLevel", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("preCheckSealOk", new TableInfo.Column("preCheckSealOk", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("rawSignalRef", new TableInfo.Column("rawSignalRef", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("snrValue", new TableInfo.Column("snrValue", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("result", new TableInfo.Column("result", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTestSessions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTestSessions.add(new TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION", Arrays.asList("patientId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTestSessions = new HashSet<TableInfo.Index>(1);
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_patientId", false, Arrays.asList("patientId"), Arrays.asList("ASC")));
        final TableInfo _infoTestSessions = new TableInfo("test_sessions", _columnsTestSessions, _foreignKeysTestSessions, _indicesTestSessions);
        final TableInfo _existingTestSessions = TableInfo.read(db, "test_sessions");
        if (!_infoTestSessions.equals(_existingTestSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "test_sessions(com.neohear.data.entity.TestSession).\n"
                  + " Expected:\n" + _infoTestSessions + "\n"
                  + " Found:\n" + _existingTestSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsReferrals = new HashMap<String, TableInfo.Column>(7);
        _columnsReferrals.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("patientId", new TableInfo.Column("patientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("testSessionId", new TableInfo.Column("testSessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferrals.put("followUpLog", new TableInfo.Column("followUpLog", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReferrals = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysReferrals.add(new TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION", Arrays.asList("patientId"), Arrays.asList("id")));
        _foreignKeysReferrals.add(new TableInfo.ForeignKey("test_sessions", "CASCADE", "NO ACTION", Arrays.asList("testSessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesReferrals = new HashSet<TableInfo.Index>(2);
        _indicesReferrals.add(new TableInfo.Index("index_referrals_patientId", false, Arrays.asList("patientId"), Arrays.asList("ASC")));
        _indicesReferrals.add(new TableInfo.Index("index_referrals_testSessionId", false, Arrays.asList("testSessionId"), Arrays.asList("ASC")));
        final TableInfo _infoReferrals = new TableInfo("referrals", _columnsReferrals, _foreignKeysReferrals, _indicesReferrals);
        final TableInfo _existingReferrals = TableInfo.read(db, "referrals");
        if (!_infoReferrals.equals(_existingReferrals)) {
          return new RoomOpenHelper.ValidationResult(false, "referrals(com.neohear.data.entity.Referral).\n"
                  + " Expected:\n" + _infoReferrals + "\n"
                  + " Found:\n" + _existingReferrals);
        }
        final HashMap<String, TableInfo.Column> _columnsRiskQuestionnaireResponses = new HashMap<String, TableInfo.Column>(5);
        _columnsRiskQuestionnaireResponses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskQuestionnaireResponses.put("patientId", new TableInfo.Column("patientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskQuestionnaireResponses.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskQuestionnaireResponses.put("answers", new TableInfo.Column("answers", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiskQuestionnaireResponses.put("riskLevel", new TableInfo.Column("riskLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRiskQuestionnaireResponses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRiskQuestionnaireResponses.add(new TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION", Arrays.asList("patientId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRiskQuestionnaireResponses = new HashSet<TableInfo.Index>(1);
        _indicesRiskQuestionnaireResponses.add(new TableInfo.Index("index_risk_questionnaire_responses_patientId", false, Arrays.asList("patientId"), Arrays.asList("ASC")));
        final TableInfo _infoRiskQuestionnaireResponses = new TableInfo("risk_questionnaire_responses", _columnsRiskQuestionnaireResponses, _foreignKeysRiskQuestionnaireResponses, _indicesRiskQuestionnaireResponses);
        final TableInfo _existingRiskQuestionnaireResponses = TableInfo.read(db, "risk_questionnaire_responses");
        if (!_infoRiskQuestionnaireResponses.equals(_existingRiskQuestionnaireResponses)) {
          return new RoomOpenHelper.ValidationResult(false, "risk_questionnaire_responses(com.neohear.data.entity.RiskQuestionnaireResponse).\n"
                  + " Expected:\n" + _infoRiskQuestionnaireResponses + "\n"
                  + " Found:\n" + _existingRiskQuestionnaireResponses);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncRecords = new HashMap<String, TableInfo.Column>(7);
        _columnsSyncRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("entity_id", new TableInfo.Column("entity_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("entity_type", new TableInfo.Column("entity_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("is_demo", new TableInfo.Column("is_demo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncRecords.put("last_attempt_at", new TableInfo.Column("last_attempt_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncRecords = new TableInfo("sync_records", _columnsSyncRecords, _foreignKeysSyncRecords, _indicesSyncRecords);
        final TableInfo _existingSyncRecords = TableInfo.read(db, "sync_records");
        if (!_infoSyncRecords.equals(_existingSyncRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_records(com.neohear.data.entity.SyncRecord).\n"
                  + " Expected:\n" + _infoSyncRecords + "\n"
                  + " Found:\n" + _existingSyncRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsCryAnalyses = new HashMap<String, TableInfo.Column>(11);
        _columnsCryAnalyses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("patientId", new TableInfo.Column("patientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("avgPitchHz", new TableInfo.Column("avgPitchHz", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("pitchStdDev", new TableInfo.Column("pitchStdDev", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("avgEnergyDb", new TableInfo.Column("avgEnergyDb", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("jitter", new TableInfo.Column("jitter", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("shimmer", new TableInfo.Column("shimmer", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("voicingRatio", new TableInfo.Column("voicingRatio", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("riskFlags", new TableInfo.Column("riskFlags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCryAnalyses.put("isExperimental", new TableInfo.Column("isExperimental", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCryAnalyses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCryAnalyses.add(new TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION", Arrays.asList("patientId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCryAnalyses = new HashSet<TableInfo.Index>(1);
        _indicesCryAnalyses.add(new TableInfo.Index("index_cry_analyses_patientId", false, Arrays.asList("patientId"), Arrays.asList("ASC")));
        final TableInfo _infoCryAnalyses = new TableInfo("cry_analyses", _columnsCryAnalyses, _foreignKeysCryAnalyses, _indicesCryAnalyses);
        final TableInfo _existingCryAnalyses = TableInfo.read(db, "cry_analyses");
        if (!_infoCryAnalyses.equals(_existingCryAnalyses)) {
          return new RoomOpenHelper.ValidationResult(false, "cry_analyses(com.neohear.data.entity.CryAnalysis).\n"
                  + " Expected:\n" + _infoCryAnalyses + "\n"
                  + " Found:\n" + _existingCryAnalyses);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4851b94555f699769ab17a33b5de0151", "1ec9036e21f2fdaa1c2723fbfe456d2e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "patients","test_sessions","referrals","risk_questionnaire_responses","sync_records","cry_analyses");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `patients`");
      _db.execSQL("DELETE FROM `test_sessions`");
      _db.execSQL("DELETE FROM `referrals`");
      _db.execSQL("DELETE FROM `risk_questionnaire_responses`");
      _db.execSQL("DELETE FROM `sync_records`");
      _db.execSQL("DELETE FROM `cry_analyses`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PatientDao.class, PatientDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TestSessionDao.class, TestSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReferralDao.class, ReferralDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RiskQuestionnaireResponseDao.class, RiskQuestionnaireResponseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DashboardDao.class, DashboardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncDao.class, SyncDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CryAnalysisDao.class, CryAnalysisDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PatientDao patientDao() {
    if (_patientDao != null) {
      return _patientDao;
    } else {
      synchronized(this) {
        if(_patientDao == null) {
          _patientDao = new PatientDao_Impl(this);
        }
        return _patientDao;
      }
    }
  }

  @Override
  public TestSessionDao testSessionDao() {
    if (_testSessionDao != null) {
      return _testSessionDao;
    } else {
      synchronized(this) {
        if(_testSessionDao == null) {
          _testSessionDao = new TestSessionDao_Impl(this);
        }
        return _testSessionDao;
      }
    }
  }

  @Override
  public ReferralDao referralDao() {
    if (_referralDao != null) {
      return _referralDao;
    } else {
      synchronized(this) {
        if(_referralDao == null) {
          _referralDao = new ReferralDao_Impl(this);
        }
        return _referralDao;
      }
    }
  }

  @Override
  public RiskQuestionnaireResponseDao riskQuestionnaireResponseDao() {
    if (_riskQuestionnaireResponseDao != null) {
      return _riskQuestionnaireResponseDao;
    } else {
      synchronized(this) {
        if(_riskQuestionnaireResponseDao == null) {
          _riskQuestionnaireResponseDao = new RiskQuestionnaireResponseDao_Impl(this);
        }
        return _riskQuestionnaireResponseDao;
      }
    }
  }

  @Override
  public DashboardDao dashboardDao() {
    if (_dashboardDao != null) {
      return _dashboardDao;
    } else {
      synchronized(this) {
        if(_dashboardDao == null) {
          _dashboardDao = new DashboardDao_Impl(this);
        }
        return _dashboardDao;
      }
    }
  }

  @Override
  public SyncDao syncDao() {
    if (_syncDao != null) {
      return _syncDao;
    } else {
      synchronized(this) {
        if(_syncDao == null) {
          _syncDao = new SyncDao_Impl(this);
        }
        return _syncDao;
      }
    }
  }

  @Override
  public CryAnalysisDao cryAnalysisDao() {
    if (_cryAnalysisDao != null) {
      return _cryAnalysisDao;
    } else {
      synchronized(this) {
        if(_cryAnalysisDao == null) {
          _cryAnalysisDao = new CryAnalysisDao_Impl(this);
        }
        return _cryAnalysisDao;
      }
    }
  }
}
