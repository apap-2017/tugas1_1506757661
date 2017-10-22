package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.model.KecamatanModel;
import com.example.demo.model.KeluargaModel;
import com.example.demo.model.KelurahanModel;
import com.example.demo.model.KotaModel;
import com.example.demo.model.PendudukModel;


@Mapper
public interface SidukMapper {
	
	 @Select("select * from penduduk p join keluarga k "+
	 "join kelurahan kel join kecamatan kec join kota kot " + 
	 "where p.id_keluarga = k.id and k.id_kelurahan = kel.id and kel.id_kecamatan = kec.id " + 
	 "and kec.id_kota = kot.id and p.nik =#{nik}")
	    @Results(value = {
	    	@Result(property="id", column="id"),
	    	@Result(property="nik", column="nik"),
	    	@Result(property="nama", column="nama"),
	    	@Result(property="tempat_lahir", column="tempat_lahir"),
	    	@Result(property="tanggal_lahir", column="tanggal_lahir"),
	    	@Result(property="jenis_kelamin", column="jenis_kelamin"),
	    	@Result(property="is_wni", column="is_wni"),
	    	@Result(property="id_keluarga", column="id_keluarga"),
	    	@Result(property="agama", column="agama"),
	    	@Result(property="pekerjaan", column="pekerjaan"),
	    	@Result(property="status_perkawinan", column="status_perkawinan"),
	    	@Result(property="status_dalam_keluarga", column="status_dalam_keluarga"),
	    	@Result(property="golongan_darah", column="golongan_darah"),
	    	@Result(property="is_wafat", column="is_wafat"),
	    	@Result(property="alamat", column="alamat"),
	    	@Result(property="rt", column="rt"),
	    	@Result(property="rw", column="rw"),
	    	@Result(property="kelurahan", column="nama_kelurahan"),
	    	@Result(property="kecamatan", column="nama_kecamatan"),
	    	@Result(property="kota", column="nama_kota")	    	
	    })
	    PendudukModel selectPenduduk (@Param("nik") String nik);
	    
	 
	 
	@Select("select p.nama, p.nik, p.jenis_kelamin, p.tempat_lahir, p.tanggal_lahir, p.agama, " +
	"p.pekerjaan, p.status_perkawinan, p.status_dalam_keluarga, p.is_wni, p.is_wafat " +
	"from penduduk p join keluarga k where p.id_keluarga = k.id and k.nomor_kk = #{nomor_kk}")
	List<PendudukModel> selectAnggotaKeluarga(String nomor_kk);
	

	 
	 @Select("select k.nomor_kk, k.alamat, k.RT, k.RW, k.id_kelurahan, k.is_tidak_berlaku, kel.nama_kelurahan, " +
	 "kec.nama_kecamatan, kot.nama_kota from keluarga k join kelurahan kel " +
	 "join kecamatan kec join kota kot where k.id_kelurahan = kel.id and " +
	 "kel.id_kecamatan = kec.id and kec.id_kota = kot.id and k.nomor_kk = #{nkk}")
	    @Results(value = {
	    	@Result(property="id", column="id"),
	    	@Result(property="nomor_kk", column="nomor_kk"),
	    	@Result(property="alamat", column="alamat"),
	    	@Result(property="rt", column="rt"),
	    	@Result(property="rw", column="rw"),
	    	@Result(property="id_kelurahan", column="id_kelurahan"),
	    	@Result(property="is_tidak_berlaku", column="is_tidak_berlaku"),
	    	@Result(property="kelurahan", column="nama_kelurahan"),
	    	@Result(property="kecamatan", column="nama_kecamatan"),
	    	@Result(property="kota", column="nama_kota"),
	    	@Result(property="anggotaKeluarga", column="nomor_kk",
	    			javaType = List.class,
	    			many=@Many(select="selectAnggotaKeluarga"))
	    	
	    })	 
		KeluargaModel selectKeluarga(@Param("nkk") String nkk);
	 
	 	@Select("select k.nomor_kk from keluarga k where k.id = #{id}")
		String selectKeluargaNKKByID(long id);

	 
	 
	 
	 	@Insert("INSERT INTO penduduk (id, nik, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,"  +
	 	"is_wni, id_keluarga, agama, pekerjaan, status_perkawinan, status_dalam_keluarga, " +
	 	"golongan_darah, is_wafat) VALUES (#{id}, #{nik}, #{nama}, #{tempat_lahir}, #{tanggal_lahir}, #{jenis_kelamin}, #{is_wni},"+
	 	"#{id_keluarga}, #{agama}, #{pekerjaan}, #{status_perkawinan}, #{status_dalam_keluarga}, #{golongan_darah},"+
	 	"#{is_wafat})")
	 	void addPenduduk(PendudukModel penduduk);
	 
	 //mengambil kode kelurahan berdasarkan id kelurahan yg dimiliki keluarga
	 @Select("select kel.kode_kelurahan from keluarga k join kelurahan kel where k.id_kelurahan = kel.id and k.id = #{id_kelurahan}")
	 String selectKodeKelurahan(@Param("id_kelurahan") String id_kelurahan);

	 @Select("select count(*) from penduduk")
	 int countPendudukRow();
	 
	 @Insert("INSERT INTO keluarga (id, nomor_kk, alamat, rt, rw, id_kelurahan,"  +
			 	"is_tidak_berlaku) VALUES (#{id}, #{nomor_kk}, #{alamat}, #{rt}, #{rw}, #{id_kelurahan}, #{is_tidak_berlaku})")
			 	void addKeluarga(KeluargaModel keluarga);
		
	 //mengambil id kelurahan berdasarkan nama kelurahan yg dimiliki keluarga
	 @Select("select id from kelurahan where nama_kelurahan = #{nama_kelurahan}")
	 String selectIDKelurahan(@Param("nama_kelurahan") String nama_kelurahan);
	 
		 

	 @Select("select count(*) from keluarga")
	 int countKeluargaRow();
	 
	 
	 @Select("select * from kelurahan where id = #{id}")
	    @Results(value = {
	    	@Result(property="id", column="id"),
	    	@Result(property="id_kecamatan", column="id_kecamatan"),
	    	@Result(property="kode_kelurahan", column="kode_kelurahan"),
	    	@Result(property="nama_kelurahan", column="nama_kelurahan"),
	    	@Result(property="kode_pos", column="kode_pos")
	    })	 
		KelurahanModel selectKelurahanByID(long id);
	 

	 @Select("select * from kecamatan where id = #{id}")
	    @Results(value = {
	    	@Result(property="id", column="id"),
	    	@Result(property="id_kota", column="id_kota"),
	    	@Result(property="kode_kecamatan", column="kode_kecamatan"),
	    	@Result(property="nama_kecamatan", column="nama_kecamatan")
	    })
		KecamatanModel selectKecamatanByID(long id);
	 

	 @Select("select * from kota where id = #{id}")
	    @Results(value = {
	    	@Result(property="id", column="id"),
	    	@Result(property="kode_kota", column="kode_kota"),
	    	@Result(property="nama_kota", column="nama_kota")
	    })
		KotaModel selectKotaByID(long id);

	    @Update("UPDATE penduduk SET nik = #{nik}, nama = #{nama}, tempat_lahir = #{tempat_lahir}, tanggal_lahir = #{tanggal_lahir}"
	    		+ ", jenis_kelamin = #{jenis_kelamin}, is_wni = #{is_wni}, id_keluarga = #{id_keluarga}"
	    		+ ", agama = #{agama}, pekerjaan = #{pekerjaan}, status_perkawinan = #{status_perkawinan}"
	    		+ ", status_perkawinan = #{status_perkawinan}, status_dalam_keluarga = #{status_dalam_keluarga}, "
	    		+ "golongan_darah = #{golongan_darah}, is_wafat = #{is_wafat} WHERE nik = #{nik_lama}")	
	    void updatePenduduk (PendudukModel penduduk);
	    
	    @Update("UPDATE keluarga SET nomor_kk = #{nomor_kk}, alamat = #{alamat}, rt = #{rt}, rw = #{rw}"
	    		+ ", id_kelurahan = #{id_kelurahan}, is_tidak_berlaku = #{is_tidak_berlaku} WHERE nomor_kk = #{nomor_kk_lama}")	
	    void updateKeluarga (KeluargaModel keluarga);
	    
	    @Update("UPDATE penduduk SET is_wafat = 1 WHERE nik = #{nik_lama}")	
	    void nonAktifkan (String nik);
	    
	    @Update("UPDATE keluarga SET is_tidak_aktif = 1 WHERE nomor_kk = #{nkk}")	
	    void nonAktifkanKeluarga (String nkk);
	    
	    
	    @Select("select * from kota")
	    List<KotaModel> selectAllKota();
	    
	    @Select("select * from kecamatan where id_kota = #{id}")
		List<KecamatanModel> selectKecamatanByIDKota(long id);
	    
	    @Select("select * from kelurahan where id_kecamatan = #{id}")
		List<KelurahanModel> selectKelurahanByIDKecamatan(long id);
	    
	    @Select("select * from penduduk p join keluarga k "+
	    		 "join kelurahan kel join kecamatan kec join kota kot " + 
	    		 "where p.id_keluarga = k.id and k.id_kelurahan = kel.id and kel.id_kecamatan = kec.id " + 
	    		 "and kec.id_kota = kot.id and kot.id=#{id_kota} and kec.id=#{id_kecamatan} and kel.id=#{id_kelurahan}")
	    List<PendudukModel> selectPendudukByDomisili(@Param("id_kota") long id_kota,
	    		@Param("id_kecamatan") long id_kecamatan,
	    		@Param("id_kelurahan") long id_kelurahan);


}
