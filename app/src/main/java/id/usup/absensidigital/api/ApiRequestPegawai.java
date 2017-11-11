package id.usup.absensidigital.api;

import id.usup.absensidigital.model.Value;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by acer on 10/30/2017.
 */

public interface ApiRequestPegawai {

    @FormUrlEncoded
    @POST("insert.php")
    Call<Value> sendDataPegawai(@Field("nip")String nip,
                                @Field("nama") String nama,
                                @Field("alamat") String alamat,
                                @Field("jk") String jenisKelamin,
                                @Field("nohp") String noHp,
                                @Field("imei") String imei
    );
}
