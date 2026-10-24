package com.akillifinans.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StockDao_Impl implements StockDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StockEntity> __insertionAdapterOfStockEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStockData;

  public StockDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStockEntity = new EntityInsertionAdapter<StockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `stocks` (`symbol`,`name`,`sector`,`currentPrice`,`dailyChangePercent`,`fk`,`pddd`,`isFavorite`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StockEntity entity) {
        statement.bindString(1, entity.getSymbol());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSector());
        statement.bindDouble(4, entity.getCurrentPrice());
        statement.bindDouble(5, entity.getDailyChangePercent());
        statement.bindDouble(6, entity.getFk());
        statement.bindDouble(7, entity.getPddd());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE stocks SET isFavorite = ? WHERE symbol = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStockData = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE stocks \n"
                + "        SET name = ?,\n"
                + "            sector = ?,\n"
                + "            currentPrice = ?,\n"
                + "            dailyChangePercent = ?,\n"
                + "            fk = ?,\n"
                + "            pddd = ?\n"
                + "        WHERE symbol = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insertStocks(final List<StockEntity> stocks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStockEntity.insert(stocks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFavoriteStatus(final String symbol, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, symbol);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStockData(final String symbol, final String name, final String sector,
      final double currentPrice, final double dailyChangePercent, final double fk,
      final double pddd, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStockData.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, name);
        _argIndex = 2;
        _stmt.bindString(_argIndex, sector);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, currentPrice);
        _argIndex = 4;
        _stmt.bindDouble(_argIndex, dailyChangePercent);
        _argIndex = 5;
        _stmt.bindDouble(_argIndex, fk);
        _argIndex = 6;
        _stmt.bindDouble(_argIndex, pddd);
        _argIndex = 7;
        _stmt.bindString(_argIndex, symbol);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStockData.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StockEntity>> getAllStocks() {
    final String _sql = "SELECT * FROM stocks ORDER BY symbol ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stocks"}, new Callable<List<StockEntity>>() {
      @Override
      @NonNull
      public List<StockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfCurrentPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPrice");
          final int _cursorIndexOfDailyChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyChangePercent");
          final int _cursorIndexOfFk = CursorUtil.getColumnIndexOrThrow(_cursor, "fk");
          final int _cursorIndexOfPddd = CursorUtil.getColumnIndexOrThrow(_cursor, "pddd");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<StockEntity> _result = new ArrayList<StockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockEntity _item;
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final double _tmpCurrentPrice;
            _tmpCurrentPrice = _cursor.getDouble(_cursorIndexOfCurrentPrice);
            final double _tmpDailyChangePercent;
            _tmpDailyChangePercent = _cursor.getDouble(_cursorIndexOfDailyChangePercent);
            final double _tmpFk;
            _tmpFk = _cursor.getDouble(_cursorIndexOfFk);
            final double _tmpPddd;
            _tmpPddd = _cursor.getDouble(_cursorIndexOfPddd);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _item = new StockEntity(_tmpSymbol,_tmpName,_tmpSector,_tmpCurrentPrice,_tmpDailyChangePercent,_tmpFk,_tmpPddd,_tmpIsFavorite);
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
  public Flow<List<StockEntity>> getFavoriteStocks() {
    final String _sql = "SELECT * FROM stocks WHERE isFavorite = 1 ORDER BY symbol ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stocks"}, new Callable<List<StockEntity>>() {
      @Override
      @NonNull
      public List<StockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfCurrentPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPrice");
          final int _cursorIndexOfDailyChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyChangePercent");
          final int _cursorIndexOfFk = CursorUtil.getColumnIndexOrThrow(_cursor, "fk");
          final int _cursorIndexOfPddd = CursorUtil.getColumnIndexOrThrow(_cursor, "pddd");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<StockEntity> _result = new ArrayList<StockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockEntity _item;
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final double _tmpCurrentPrice;
            _tmpCurrentPrice = _cursor.getDouble(_cursorIndexOfCurrentPrice);
            final double _tmpDailyChangePercent;
            _tmpDailyChangePercent = _cursor.getDouble(_cursorIndexOfDailyChangePercent);
            final double _tmpFk;
            _tmpFk = _cursor.getDouble(_cursorIndexOfFk);
            final double _tmpPddd;
            _tmpPddd = _cursor.getDouble(_cursorIndexOfPddd);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _item = new StockEntity(_tmpSymbol,_tmpName,_tmpSector,_tmpCurrentPrice,_tmpDailyChangePercent,_tmpFk,_tmpPddd,_tmpIsFavorite);
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
  public Object exists(final String symbol, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM stocks WHERE symbol = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, symbol);
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
  public Object isFavorite(final String symbol, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT isFavorite FROM stocks WHERE symbol = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, symbol);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @Nullable
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
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
  public Object getStockBySymbol(final String symbol,
      final Continuation<? super StockEntity> $completion) {
    final String _sql = "SELECT * FROM stocks WHERE symbol = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, symbol);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StockEntity>() {
      @Override
      @Nullable
      public StockEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfCurrentPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPrice");
          final int _cursorIndexOfDailyChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyChangePercent");
          final int _cursorIndexOfFk = CursorUtil.getColumnIndexOrThrow(_cursor, "fk");
          final int _cursorIndexOfPddd = CursorUtil.getColumnIndexOrThrow(_cursor, "pddd");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final StockEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final double _tmpCurrentPrice;
            _tmpCurrentPrice = _cursor.getDouble(_cursorIndexOfCurrentPrice);
            final double _tmpDailyChangePercent;
            _tmpDailyChangePercent = _cursor.getDouble(_cursorIndexOfDailyChangePercent);
            final double _tmpFk;
            _tmpFk = _cursor.getDouble(_cursorIndexOfFk);
            final double _tmpPddd;
            _tmpPddd = _cursor.getDouble(_cursorIndexOfPddd);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _result = new StockEntity(_tmpSymbol,_tmpName,_tmpSector,_tmpCurrentPrice,_tmpDailyChangePercent,_tmpFk,_tmpPddd,_tmpIsFavorite);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
