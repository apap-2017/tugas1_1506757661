package com.example.demo.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.KecamatanModel;
import com.example.demo.model.KeluargaModel;
import com.example.demo.model.KelurahanModel;
import com.example.demo.model.KotaModel;
import com.example.demo.model.PendudukModel;
import com.example.demo.service.SidukService;


@Controller
public class SidukController {
	
	@Autowired
	SidukService sidukDAO;
	
    @RequestMapping("/")
    public String home ()
    {
        return "home";
    }
    
    boolean sudahNonAktif = false;
    
    @RequestMapping("/penduduk")
	public String penduduk (@RequestParam(value = "nik", required = false, defaultValue = "tes") String nik, Model model) 
	{
    	if (sudahNonAktif == true) {
    		model.addAttribute("nik", nik);
    		sudahNonAktif = false;
    		return "sukses-penduduk-nonaktif";
    	} else {
    		if (nik.equals("tes")) {
				model.addAttribute("message", "NIK tidak ada");
				return "error";
			}
			PendudukModel penduduk = sidukDAO.selectPenduduk(nik);

			if (penduduk == null) {
				model.addAttribute("message", "Penduduk tidak ditemukan");
				return "error";
			}
    		
			penduduk.changeDefault(nik);
    		model.addAttribute("penduduk", penduduk);
    		return "data-penduduk";
    	}
    	
	}
    
	@RequestMapping(value = "/penduduk/mati", method = RequestMethod.POST)
	public String nonAktif (@ModelAttribute PendudukModel penduduk) 
	{
		String nik = penduduk.getNik();
		long idKeluarga = penduduk.getId_keluarga();
		String nkk = sidukDAO.selectKeluargaNKKByID(idKeluarga);
		sidukDAO.nonAktifkan(nik);
		sudahNonAktif = true;
		int jumlahKeluargaAktif = 0;
		List<PendudukModel> anggotaKeluarga = sidukDAO.selectAnggotaKeluarga(nkk);
		int jumlahKeluarga = anggotaKeluarga.size();
		for (int i = 0; i < jumlahKeluarga; i++) {
			if (anggotaKeluarga.get(i).getIs_wafat().equals("0")) {
				jumlahKeluargaAktif++;
			}
		}
		if (jumlahKeluarga != 0 && jumlahKeluargaAktif == 0) {
			sidukDAO.nonAktifkanKeluarga(nkk);
		}
		
		return "redirect:/penduduk?nik=" + nik;
	}
	
    @RequestMapping("/keluarga")
   	public String keluarga (@RequestParam(value = "nkk", required = false, defaultValue = "tes") String nkk, Model model) 
   	{
       	
    	if (nkk.equals("tes")) {
			model.addAttribute("message", "NKK tidak ada");
			return "error";
		}
		KeluargaModel keluarga = sidukDAO.selectKeluarga(nkk);

		if (keluarga == null) {
			model.addAttribute("message", "Keluarga tidak ditemukan");
			return "error";
		}
       	int sizeAnggotaKeluarga = keluarga.getAnggotaKeluarga().size();
       	if (sizeAnggotaKeluarga != 0) {
       		for (int i = 0; i < sizeAnggotaKeluarga; i++) {
       			PendudukModel index = keluarga.getAnggotaKeluarga().get(i);
       			String nik = index.getNik();
       			keluarga.getAnggotaKeluarga().get(i).changeDefault(nik);
       		}
       	}
       	model.addAttribute("keluarga", keluarga);
   		return "data-keluarga";
   	}
    
    @RequestMapping("/penduduk/tambah")
    public String pendudukTambah (Model model)
    {
    	PendudukModel penduduk = new PendudukModel();

    	 model.addAttribute ("penduduk", penduduk);
        return "form-penduduk-tambah";
    }
    
    @RequestMapping(value= "/penduduk/tambah", method = RequestMethod.POST)
    public String pendudukTambahSubmit (@ModelAttribute PendudukModel penduduk, Model model)
    {

    	int id = sidukDAO.countPendudukRow();
    	penduduk.setId(id+1);
    	long id_keluarga = penduduk.getId_keluarga();
    	String idString = String.valueOf(id_keluarga);
    	//cari id kelurahan sesuai id keluarga
    	String kode_kelurahan = sidukDAO.selectKodeKelurahan(idString);
    	//firstSix = ambil string 0,5 -> 6 digit pertama
    	String firstSix = kode_kelurahan.substring(0, 6);
    	String tanggal_lahir = penduduk.getTanggal_lahir();    	
    	String[] tanggal_lahir_split = tanggal_lahir.split("-");
    	String tanggal = tanggal_lahir_split[2];
    	String bulan = tanggal_lahir_split[1];
    	String tahun = tanggal_lahir_split[0].substring(2);
    	StringBuilder tglAppend = new StringBuilder().append(tanggal).append(bulan).append(tahun);
    	
    	String tanggalFix = tglAppend.toString();
    	String jenis_kelamin = penduduk.getJenis_kelamin();
    	
    	if (jenis_kelamin.equals("Wanita")) {
    		long tanggalLahir = Long.parseLong(tanggal);
    		tanggalLahir = tanggalLahir + 40;
    		tanggal_lahir = String.valueOf(tanggalLahir);
    		StringBuilder tglBaru = new StringBuilder().append(tanggal_lahir).append(bulan).append(tahun);
    		tanggalFix = tglBaru.toString();
    	} 
    	String secondSix = tanggalFix;
    	String nomor_urut = "0001";
    	int counter = 1;
    	StringBuilder nikAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut);
    	String nik = nikAppend.toString();
    	//generate nik -> append semua + 0001
    	//loop
    	boolean masihAda = true;
    	while (masihAda == true) {
            PendudukModel cekPenduduk = sidukDAO.selectPenduduk (nik);
    		if (cekPenduduk != null) {
    			counter+=1;
    			int digit_counter = String.valueOf(counter).length();
    			StringBuilder nomor_urut_baru = new StringBuilder();
    			if (digit_counter == 1) {
        			 nomor_urut_baru = new StringBuilder().append("000").append(String.valueOf(counter));
    			} else if (digit_counter == 2) {
        			 nomor_urut_baru = new StringBuilder().append("00").append(String.valueOf(counter));
    			} else if (digit_counter == 3) {
        			 nomor_urut_baru = new StringBuilder().append("0").append(String.valueOf(counter));
    			} else {
    				 nomor_urut_baru = new StringBuilder().append(String.valueOf(counter));
    			}
    			StringBuilder nikBaruAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut_baru.toString());
    	    	String nikBaru = nikBaruAppend.toString();
    	    	nik = nikBaru;    	    	
    		} else {
    			masihAda = false;
    		}
    	} 
    	penduduk.setNik(nik);

    	//set wni sesuai kalo wni = 1
    	//set iswafat sesuai kalo hidup = 0
    	//set jenis kelamin sesuai-kalo cowok = 0
    	
    	if (penduduk.getIs_wni().equals("WNI") || penduduk.getIs_wni().equals("wni")) {
    		penduduk.setIs_wni("1");
    	} else {
    		penduduk.setIs_wni("0");
    	}
    	
    	if (penduduk.getIs_wafat().equals("Hidup")) {
    		penduduk.setIs_wafat("0");
    	} else {
    		penduduk.setIs_wafat("1");
    	}
    	
    	if (penduduk.getJenis_kelamin().equals("Pria")) {
    		penduduk.setJenis_kelamin("0");
    	} else {
    		penduduk.setJenis_kelamin("1");
    	}
    	
    	
    	String tanggal_lahir_fix = penduduk.getTanggal_lahir();    	
    	String[] tanggal_lahir_split_fix = tanggal_lahir_fix.split("-");
    	String tanggal_fix = tanggal_lahir_split_fix[2];
    	String bulan_fix = tanggal_lahir_split_fix[1];
    	String tahun_fix = tanggal_lahir_split_fix[0];
    	StringBuilder tglAppendFix = new StringBuilder().append(tahun_fix).append("/").append(bulan_fix).append("/").append(tanggal_fix);
    	
    	penduduk.setTanggal_lahir(tglAppendFix.toString());

    	sidukDAO.addPenduduk (penduduk);
    	model.addAttribute("nikbaru", nik);
        return "sukses-data-penduduk-tambah";
    }
    
    @RequestMapping("/keluarga/tambah")
    public String keluargaTambah (Model model)
    {
    	KeluargaModel keluarga = new KeluargaModel();

    	 model.addAttribute ("keluarga", keluarga);
        return "form-keluarga-tambah";
    }
    
    @RequestMapping(value= "/keluarga/tambah", method = RequestMethod.POST)
    public String keluargaTambahSubmit (@ModelAttribute KeluargaModel keluarga, Model model)
    {
    	int id = sidukDAO.countKeluargaRow();
    	keluarga.setId(id+1);
    	String nama_kelurahan = keluarga.getKelurahan();
    	//cari id kelurahan sesuai nama kelurahan
    	String id_kelurahan = sidukDAO.selectIDKelurahan(nama_kelurahan);
    	keluarga.setId_kelurahan(id_kelurahan);
    	long id_kelurahan_long = Long.parseLong(id_kelurahan);
    	KelurahanModel tes = sidukDAO.selectKelurahanByID(id_kelurahan_long);
    	String kode_kelurahan = tes.getKode_kelurahan();
    	//firstSix = ambil string 0,6 -> 6 digit pertama
    	String firstSix = kode_kelurahan.substring(0, 6);
    	
    	
    	//tanggal submit adalah tanggal sekarang
    	DateFormat df = new SimpleDateFormat("dd/MM/yy");
    	Date dateobj = new Date();
    	String tanggal_submit = df.format(dateobj);
    	
    	String[] tanggal_split = tanggal_submit.split("/");
    	String tanggal = tanggal_split[0];
    	String bulan = tanggal_split[1];
    	String tahun = tanggal_split[2];
    	StringBuilder tglAppend = new StringBuilder().append(tanggal).append(bulan).append(tahun);
    	
    	String tanggalFix = tglAppend.toString();
    	
    	String secondSix = tanggalFix;
    	String nomor_urut = "0001";
    	int counter = 1;
    	StringBuilder nkkAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut);
    	String nkk = nkkAppend.toString();
    	//generate nik -> append semua + 0001
    	//loop
    	boolean masihAda = true;
    	while (masihAda == true) {
            KeluargaModel cekKeluarga = sidukDAO.selectKeluarga (nkk);
    		if (cekKeluarga != null) {
    			counter+=1;
    			int digit_counter = String.valueOf(counter).length();
    			StringBuilder nomor_urut_baru = new StringBuilder();
    			if (digit_counter == 1) {
        			 nomor_urut_baru = new StringBuilder().append("000").append(String.valueOf(counter));
    			} else if (digit_counter == 2) {
        			 nomor_urut_baru = new StringBuilder().append("00").append(String.valueOf(counter));
    			} else if (digit_counter == 3) {
        			 nomor_urut_baru = new StringBuilder().append("0").append(String.valueOf(counter));
    			} else {
    				 nomor_urut_baru = new StringBuilder().append(String.valueOf(counter));
    			}
    			StringBuilder nkkBaruAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut_baru.toString());
    	    	String nkkBaru = nkkBaruAppend.toString();
    	    	nkk = nkkBaru;    	    	
    		} else {
    			masihAda = false;
    		}
    	} 
    	keluarga.setNomor_kk(nkk);
    	keluarga.setIs_tidak_berlaku("0");

    	sidukDAO.addKeluarga (keluarga);
    	model.addAttribute("nkkbaru", nkk);
        return "sukses-data-keluarga-tambah";
    }
    
    @RequestMapping("/penduduk/ubah/{nik_lama}")
    public String updatePenduduk (Model model, @PathVariable(value = "nik_lama") String nik_lama)
    {
    	  PendudukModel penduduk = sidukDAO.selectPenduduk (nik_lama);
    	  if (penduduk.getIs_wni().equals("1")) {
      		penduduk.setIs_wni("WNI");
      	} else {
      		penduduk.setIs_wni("WNA");
      	}
      	
      	if (penduduk.getIs_wafat().equals("0")) {
      		penduduk.setIs_wafat("Hidup");
      	} else {
      		penduduk.setIs_wafat("Mati");
      	}
      	
      	String jenis_kelamin = penduduk.getJenis_kelamin();
      	if (jenis_kelamin.equals("0")) {
      		penduduk.setJenis_kelamin("Pria");
      	} else {
      		penduduk.setJenis_kelamin("Wanita");
      	}	
    	  model.addAttribute("penduduk", penduduk);
    	  return "form-penduduk-update";
          
    }
    
    @RequestMapping(value = "/penduduk/ubah/{nik_lama}", method = RequestMethod.POST)
    public String updatePendudukSubmit (@ModelAttribute PendudukModel penduduk,  Model model, @PathVariable(value = "nik_lama") String nik_lama)
    {
    	String niklama= nik_lama;	
    	//sesuain yg diinput kayak di db
    	//set wni sesuai kalo wni = 1
    	//set iswafat sesuai kalo hidup = 0
    	//set jenis kelamin sesuai-kalo cowok = 0
    	
    	if (penduduk.getIs_wni().equals("WNI") || penduduk.getIs_wni().equals("wni")) {
    		penduduk.setIs_wni("1");
    	} else {
    		penduduk.setIs_wni("0");
    	}
    	
    	if (penduduk.getIs_wafat().equals("Hidup")) {
    		penduduk.setIs_wafat("0");
    	} else {
    		penduduk.setIs_wafat("1");
    	}
    	
    	String jenis_kelaminawal = penduduk.getJenis_kelamin();
    	if (jenis_kelaminawal.equals("Pria")) {
    		penduduk.setJenis_kelamin("0");
    	} else {
    		penduduk.setJenis_kelamin("1");
    	}
    	String jenis_kelamin = penduduk.getJenis_kelamin();
    	
    
    	//GENERATING NEW NIK
    	boolean nikBerubah = false;
    	PendudukModel pendudukDiDB = sidukDAO.selectPenduduk(nik_lama);
    	penduduk.setNik_lama(nik_lama);
    	String firstSixlama = pendudukDiDB.getNik().substring(0,6);
    	String secondSixlama = pendudukDiDB.getNik().substring(6,12); 
    	String firstSix = firstSixlama;
    	String secondSix = secondSixlama;
    	
    	long id_keluarga = penduduk.getId_keluarga();
    	String idString = String.valueOf(id_keluarga);
    	//cari id kelurahan sesuai id keluarga
    	String kode_kelurahan = sidukDAO.selectKodeKelurahan(idString);
    	String kodekelurahan1 = kode_kelurahan.substring(0, 6);
    	
    	long id_keluarga_DB = pendudukDiDB.getId_keluarga();
    	String idStringDB = String.valueOf(id_keluarga_DB);
    	String kode_kelurahan_DB = sidukDAO.selectKodeKelurahan(idStringDB);
    	String kodekelurahan2 = kode_kelurahan_DB.substring(0, 6);
    	
    	boolean domisiliTetap = (kodekelurahan1.equals(kodekelurahan2));
    	boolean tglTetap= penduduk.getTanggal_lahir().equals(pendudukDiDB.getTanggal_lahir());
    	boolean jkTetap = penduduk.getJenis_kelamin().equals(pendudukDiDB.getJenis_kelamin());
    	if (!domisiliTetap) {
    		nikBerubah = true;
	    	firstSix = kodekelurahan1;
    		if (!tglTetap) {
    	    	String tanggal_lahir = penduduk.getTanggal_lahir();    	
    	    	String[] tanggal_lahir_split = tanggal_lahir.split("-");
    	    	String tanggal = tanggal_lahir_split[2];
    	    	String bulan = tanggal_lahir_split[1];
    	    	String tahun = tanggal_lahir_split[0].substring(2);
    	    	StringBuilder tglAppend = new StringBuilder().append(tanggal).append(bulan).append(tahun);
    	    	
    	    	String tanggalFix = tglAppend.toString();
    			secondSix = tanggalFix;
    			//jenis kelamin yg diinput
    	    	if (jenis_kelamin.equals("1")) {
    	    		long tanggalLahir = Long.parseLong(tanggal);
    	    		tanggalLahir = tanggalLahir + 40;
    	    		tanggal_lahir = String.valueOf(tanggalLahir);
    	    		StringBuilder tglBaru = new StringBuilder().append(tanggal_lahir).append(bulan).append(tahun);
    	    		tanggalFix = tglBaru.toString();
    	    	} 
    	    	secondSix = tanggalFix;
        	//tgl tetap sama 	
    		} else {
    			String tanggal_lahir = secondSixlama.substring(0,2);
    			String bulan = secondSixlama.substring(2,4);
    			String tahun = secondSixlama.substring(4,6);
    			//cek jenis kelamin pria atau wanita. jika wanita akan ditambahkan.
    			if (!jkTetap) {
    				long tanggalLahir = Long.parseLong(tanggal_lahir);
    				if (jenis_kelamin.equals("1")) {
        	    		tanggalLahir = tanggalLahir + 40;
        			} else {
        				tanggalLahir = tanggalLahir - 40;
        			}
    				tanggal_lahir = String.valueOf(tanggalLahir);
    	    		StringBuilder tglBaru = new StringBuilder().append(tanggal_lahir).append(bulan).append(tahun);
    	    		secondSix = tglBaru.toString();
    	    	} 
    		} 
    	} else {
    		if (!tglTetap) {
    			nikBerubah = true;
    			//genereate tgl lahir
    			//first six
    			//secondsix = 
    	    	String tanggal_lahir = penduduk.getTanggal_lahir();
    	    	String[] tanggal_lahir_split = tanggal_lahir.split("-");
    	    	String tanggal = tanggal_lahir_split[2];
    	    	String bulan = tanggal_lahir_split[1];
    	    	String tahun = tanggal_lahir_split[0].substring(2);
    	    	StringBuilder tglAppend = new StringBuilder().append(tanggal).append(bulan).append(tahun);
    	    	
    	    	String tanggalFix = tglAppend.toString();
    		if (jenis_kelamin.equals("1")) {
    	    		long tanggalLahir = Long.parseLong(tanggal);
    	    		tanggalLahir = tanggalLahir + 40;
    	    		tanggal_lahir = String.valueOf(tanggalLahir);
    	    		StringBuilder tglBaru = new StringBuilder().append(tanggal_lahir).append(bulan).append(tahun);
    	    		tanggalFix = tglBaru.toString();
    	    	} 
    	    	secondSix = tanggalFix;
        	//tgl tetap sama 	
    		} else {
    			String tanggal_lahir = secondSixlama.substring(0,2);
    			String bulan = secondSixlama.substring(2,4);
    			String tahun = secondSixlama.substring(4,6);
    			//cek jenis kelamin pria atau wanita. jika wanita akan ditambahkan.
    			if (!jkTetap) {
    				nikBerubah = true;
    				long tanggalLahir = Long.parseLong(tanggal_lahir);
    				if (jenis_kelamin.equals("1")) {
        	    		tanggalLahir = tanggalLahir + 40;
        			} else {
        				tanggalLahir = tanggalLahir - 40;
        			}
    				tanggal_lahir = String.valueOf(tanggalLahir);
    	    		StringBuilder tglBaru = new StringBuilder().append(tanggal_lahir).append(bulan).append(tahun);
    	    		secondSix = tglBaru.toString();
    	    	} 
    		} 
    	} 
    	
    	//JIKA NIK BERUBAH, MAKA GENEREATE NIK ULANG
    	if (nikBerubah == true) {
	    	String nomor_urut = "0001";
	    	int counter = 1;
	    	StringBuilder nikAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut);
	    	String nik = nikAppend.toString();
	    	//generate nik -> append semua + 0001
	    	//loop
	    	boolean masihAda = true;
	    	while (masihAda == true) {
	            PendudukModel cekPenduduk = sidukDAO.selectPenduduk (nik);
	    		if (cekPenduduk != null) {
	    			counter+=1;
	    			int digit_counter = String.valueOf(counter).length();
	    			StringBuilder nomor_urut_baru = new StringBuilder();
	    			if (digit_counter == 1) {
	        			 nomor_urut_baru = new StringBuilder().append("000").append(String.valueOf(counter));
	    			} else if (digit_counter == 2) {
	        			 nomor_urut_baru = new StringBuilder().append("00").append(String.valueOf(counter));
	    			} else if (digit_counter == 3) {
	        			 nomor_urut_baru = new StringBuilder().append("0").append(String.valueOf(counter));
	    			} else {
	    				 nomor_urut_baru = new StringBuilder().append(String.valueOf(counter));
	    			}
	    			StringBuilder nikBaruAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut_baru.toString());
	    	    	String nikBaru = nikBaruAppend.toString();
	    	    	nik = nikBaru;    	    	
	    		} else {
    			masihAda = false;
	    		}
	    	} 
	    	penduduk.setNik(nik);
    	} else {
    		penduduk.setNik(nik_lama);
    	}  
    	
   	 	sidukDAO.updatePenduduk(penduduk); 
    	  
   	 	model.addAttribute("niklama", niklama);
   	 	return "sukses-penduduk-update";
    }
    
    @RequestMapping("/keluarga/ubah/{nomor_kk_lama}")
    public String updateKeluarga (Model model, @PathVariable(value = "nomor_kk_lama") String nomor_kk_lama)
    {
    	  KeluargaModel keluarga = sidukDAO.selectKeluarga (nomor_kk_lama);
    	  
    	  model.addAttribute("keluarga", keluarga);
    	  return "form-keluarga-update";
          
    }
    
	@RequestMapping(value = "/keluarga/ubah/{nomor_kk_lama}", method = RequestMethod.POST)
	public String updatePendudukSubmit (@ModelAttribute KeluargaModel keluarga,  Model model, @PathVariable(value = "nomor_kk_lama") String nomor_kk_lama)
	    {
	      String nkklama= nomor_kk_lama; 
	    
	      //GENERATING NEW NKK
	   
	      KeluargaModel keluargaDiDB = sidukDAO.selectKeluarga(nomor_kk_lama);
	
	      keluarga.setNomor_kk_lama(nomor_kk_lama);
	
	      String firstSixlama = keluargaDiDB.getNomor_kk().substring(0,6);
	      String firstSix = firstSixlama;
	      
	    //tanggal submit adalah tanggal sekarang
	    	DateFormat df = new SimpleDateFormat("dd/MM/yy");
	    	Date dateobj = new Date();
	    	String tanggal_submit = df.format(dateobj);
	    	
	    	String[] tanggal_split = tanggal_submit.split("/");
	    	String tanggal = tanggal_split[0];
	    	String bulan = tanggal_split[1];
	    	String tahun = tanggal_split[2];
	    	StringBuilder tglAppend = new StringBuilder().append(tanggal).append(bulan).append(tahun);
	    	String tanggalFix = tglAppend.toString();
	    	
	    	String secondSix = tanggalFix;
	      String nama_kelurahan = keluarga.getKelurahan();
	      //cari id kelurahan sesuai nama kelurahan
	      String id_kelurahan = sidukDAO.selectIDKelurahan(nama_kelurahan);
	      keluarga.setId_kelurahan(id_kelurahan);
	      long id_kelurahan_long = Long.parseLong(id_kelurahan);
	      KelurahanModel tes = sidukDAO.selectKelurahanByID(id_kelurahan_long);
	      String kode_kelurahan = tes.getKode_kelurahan();
	      String kodekelurahan1 = kode_kelurahan.substring(0, 6);
	
	      long id_kelurahan_lama_long = Long.parseLong(keluargaDiDB.getId_kelurahan());
	      KelurahanModel tes2 = sidukDAO.selectKelurahanByID(id_kelurahan_lama_long);
	      String kode_kelurahan_lama = tes2.getKode_kelurahan();
	      String kodekelurahan2 = kode_kelurahan_lama.substring(0, 6);
	      boolean domisiliTetap = (kodekelurahan1.equals(kodekelurahan2));
	    
	      if (!domisiliTetap) {
	        firstSix = kodekelurahan1;  
	      } 
	
	      //GENEREATE NKK ULANG
	        String nomor_urut = "0001";
	        int counter = 1;
	        StringBuilder nkkAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut);
	        String nkk = nkkAppend.toString();
	        //generate nik -> append semua + 0001
	        //loop
	        boolean masihAda = true;
	        while (masihAda == true) {
	              KeluargaModel cekKeluarga = sidukDAO.selectKeluarga (nkk);
	          if (cekKeluarga != null) {
	            counter+=1;
	            int digit_counter = String.valueOf(counter).length();
	            StringBuilder nomor_urut_baru = new StringBuilder();
	            if (digit_counter == 1) {
	                 nomor_urut_baru = new StringBuilder().append("000").append(String.valueOf(counter));
	            } else if (digit_counter == 2) {
	                 nomor_urut_baru = new StringBuilder().append("00").append(String.valueOf(counter));
	            } else if (digit_counter == 3) {
	                 nomor_urut_baru = new StringBuilder().append("0").append(String.valueOf(counter));
	            } else {
	               nomor_urut_baru = new StringBuilder().append(String.valueOf(counter));
	            }
	              StringBuilder nkkBaruAppend = new StringBuilder().append(firstSix).append(secondSix).append(nomor_urut_baru.toString());
	              String nkkBaru = nkkBaruAppend.toString();
	              nkk = nkkBaru;            
	          } else {
	          masihAda = false;
	          }
	        } 
	      keluarga.setNomor_kk(nkk);
	      keluarga.setIs_tidak_berlaku("0");
	      sidukDAO.updateKeluarga(keluarga); 
	        
	      model.addAttribute("nkklama", nkklama);
	      return "sukses-keluarga-update";
	    }
	
	long idKt = 0;
	long idKc = 0;
	long idKl = 0;
	
	@RequestMapping("/penduduk/cari")
    public String cariPenduduk (Model modelKota, @RequestParam(value = "kt", required = false) String kt,
    							Model modelKec, Model modelAda, @RequestParam(value = "kc", required = false) String kc,
    							Model modelKel, Model modelAda2, 
    							Model modelAdaSemua, @RequestParam(value = "kl", required = false) String kl,
    							Model modelPenduduk,
    							Model modelJudul) throws ParseException
    {
		boolean adaSemua = false;
		if (kt == null) {
			List<KotaModel> resultKota = sidukDAO.selectAllKota();
			modelKota.addAttribute("kota", resultKota);
			modelJudul.addAttribute("judul", "Lihat Data Penduduk Berdasarkan Kota");
		} else {
			long idKota = Long.parseLong(kt);
			idKt = idKota;
			KotaModel kota = sidukDAO.selectKotaByID(idKota);
			modelKota.addAttribute("kota", kota);
			modelJudul.addAttribute("judul", "Lihat Data Penduduk Berdasarkan Kecamatan di " + kota.getNama_kota());
			if (kc == null) {
				List<KecamatanModel> resultKec = sidukDAO.selectKecamatanByIDKota(idKota);
				modelAda.addAttribute("blmAdaKelAdaKota", true);
				modelKec.addAttribute("kecamatan", resultKec);
			} else {
				long idKec = Long.parseLong(kc);
				idKc = idKec;
				KecamatanModel kec = sidukDAO.selectKecamatanByID(idKec);
				//bakal ada kelurahan setelah ini
				modelAda.addAttribute("blmAdaKelAdaKota", false);
				modelKec.addAttribute("kecamatan", kec);
				modelJudul.addAttribute("judul", "Lihat Data Penduduk Berdasarkan Kelurahan di " + kota.getNama_kota() + ", KECAMATAN " + kec.getNama_kecamatan());
				if (kl == null) {
					List<KelurahanModel> resultKel = sidukDAO.selectKelurahanByIDKecamatan(idKec);
					modelAda2.addAttribute("blmAdaKelAdaKotaAdaKec", true);
					modelKel.addAttribute("kelurahan", resultKel);
				} else {
					long idKel = Long.parseLong(kl);
					idKl = idKel;
					KelurahanModel kel = sidukDAO.selectKelurahanByID(idKel);
					//bakal ada kelurahan setelah ini
					modelAda2.addAttribute("blmAdaKelAdaKotaAdaKec", false);
					modelAdaSemua.addAttribute("adaSemua", true);
					modelKel.addAttribute("kelurahan", kel);
					modelJudul.addAttribute("judul", "Lihat Data Penduduk Berdasarkan Kelurahan di " + kota.getNama_kota() + ", KECAMATAN " + kec.getNama_kecamatan() 
					+ ", KELURAHAN " + kel.getNama_kelurahan());
					adaSemua = true;
				}
			}
		}
		
		if (adaSemua) {
			List<PendudukModel> penduduk = (ArrayList<PendudukModel>) sidukDAO.selectPendudukByDomisili(idKt, idKc, idKl);
			modelPenduduk.addAttribute("penduduk", penduduk);
			PendudukModel random1 = penduduk.get(0);
			PendudukModel random2 = penduduk.get(penduduk.size() - 1);
			
			PendudukModel tertua = random1;
			PendudukModel termuda = random2;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < penduduk.size(); i++) {
				Date tua= sdf.parse(tertua.getTanggal_lahir());
				Date muda = sdf.parse(termuda.getTanggal_lahir());
				PendudukModel indexmodel = penduduk.get(i);
				Date index = sdf.parse(indexmodel.getTanggal_lahir());
			
				//jika index before tertua, tertua = index
				if (index.before(tua)) {
					tertua = indexmodel;
				}
				//jika index after termuda, termuda = index
				if (index.after(muda)) {
					termuda = indexmodel;
				}
			}
			modelPenduduk.addAttribute("termuda", termuda);
			modelPenduduk.addAttribute("tertua", tertua);
		}
		return "cari-penduduk";
    }
	    
}

 