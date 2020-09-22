
    // pocet moznych kodovani textu
    #define CS_KOD_POCET        3

    // Tento modul je zavisly na definici techto konstant v CFG.CH :
    // CFG_CESKY_KOD a CFG_CESKY, ktere mohou mit lib. hodnotu,
    //     ale pri jejich zmene nutno znovu prelozit.
#include "_cfg.ch"
    // #define CFG_CESKY_KOD       74  // kodovani cestiny na obrazovku
    // #define CFG_CESKY           27  // lCesky

    // Jako hodnoty parametru nKodText a nKodVyst pouzivat pouze
    // konstanty CS_KOD_KAM nebo CS_KOD_LAT, jejichz skutecna hodnota je 0 a 1.
    // !!! Tyto konstanty nelze nikdy zmenit !!!

/*

MOZNOSTI DOPLNENI DALSICH KODOVANI

 Dvojrozmerna ctvercova matice KODY obsahuje klice pro konverzi.
 Rozmer je roven poctu moznych kodovani textu.
 Prvky v tabulce KODY jsou klice provadejici konverzi
 z kodovani urceneho prvnim indexem do kodovani urceneho druhym indexem.
 Prvky v tabulce KODY na hlavni diagonale by tudiz nemely smysl,
 proto jsou zde umisteny klice zhazujici ceskou diakritiku.
 Tabulku je mozno rozsirit o dalsi kodovani podle techto uvedenych
 pravidel. Dale je nutno opravit konstantu CS_KOD_POCET,
 definovat konstantu CS_KOD_NovyKod, ktera musi mit hodnotu rovnou
 ceckovskemu indexu v matici KODY a zadnou jinou !
 Kazdy klic musi mit presne 128 znaku !


PRIKLAD DOPLNENI o kodovani 602, WIN, Nov

 Pocet kodovani bude pet :
	#define CS_KOD_POCET        5

 Ctvercova matice KODY [ CS_KOD_POCET , CS_KOD_POCET ]  bude mit tvar :
{
 klic Kam-Us  , klic Kam-Lat , klic Kam-602 , klic Kam-Win , klic Kam-Nov ,
 klic Lat-Kam , klic Lat-Us  , klic Lat-602 , klic Lat-Win , klic Lat-Nov ,
 klic 602-Kam , klic 602-Lat , klic 602-Us  , klic 602-Win , klic 602-Nov ,
 klic Win-Kam , klic Win-Lat , klic Win-602 , klic Win-Us  , klic Win-Nov ,
 klic Nov-Kam , klic Nov-Lat , klic Nov-602 , klic Nov-Win , klic Nov-Us
}

 Definice konstant pro volani fce cs()
	#define CS_KOD_KAM          0	// kodovani cestiny Kamenik
	#define CS_KOD_LAT          1	// kodovani cestiny Latin 2
	#define CS_KOD_602          2	// kodovani cestiny Text602
	#define CS_KOD_WIN          3	// kodovani cestiny Windows
	#define CS_KOD_Nov          4	// kodovani cestiny NovyKod

*/


#include "clipdefs.h"
#include "fm.api"
#include "extend.api"
#include "item.api"


// Minimalni index kodu, vzdy roven nule
#define CS_KOD_MIN          0
// Maximalni index kodu, podle poctu definovanych kodovani
#define CS_KOD_MAX          CS_KOD_POCET-1

typedef BYTE  TYPKOD[129];
TYPKOD kody [CS_KOD_POCET] [CS_KOD_POCET] =
{
  // klic Kam-Us
  "CuedaDTceELIllAAEzZooOuUyOUSLYRtaiounNUOsrrR¬­®¯°±²³´µ¶·¸¹º»¼½¾¿"
  "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞßàáâãäåæçèéêëìíîïğñòóôõö÷øùúûüışÿ",

  // klic Kam-Lat
  "¬‚Ô„Ò›ŸØ·‘Ö–’µ§¦“”à…éì™šæ•íüœ ¡¢£åÕŞâçıêè¬­®¯°±²³´µ¶·¸¹º»¼½¾¿"
  "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞßàáâãäåæçèéêëìíîïğñòóôõö÷øùúûüışÿ",

  // klic Kam-Win
  "ÈüéïäÏèìÌÅÍ¾åÄÁÉôöÓùÚıÖÜŠ¼İØáíóúòÒÙÔšøàÀÈº«»----+ÁÂÌª¦¦¬-¯¿¬"
  "L+T+¦+ÃãL-¦T¦=+¤ğĞÏËïÒÍÎì----ŞÙ-ÓßÔÑñòŠšÀÚàÛıİş´­½²¡¢§÷¸°¨ÿûØø¦ ",


  // klic Lat-Kam
  "€‚ƒ„–†‡ˆ‰Š‹ŒŠ“”œŒ—˜™š†Ÿ‡ ¡¢£¤¥’‘¨©ª«€­®¯°±²³´¶‰¸¹º»¼½¾¿"
  "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑ…Óƒ¥‹×ˆÙÚÛÜİ¦ß•á§ãä¤›¨«—ªë˜îïğñòóôõö÷øùúû©şÿ",

  // klic Lat-Us
  "€ueƒau†‡ˆ‰Š‹ŒAELlooLl—˜OUTtcaiou¤¥Zz¨©ª«C­®¯°±²³´A¶E¸¹º»¼½¾¿"
  "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑDÓdNI×eÙÚÛÜİUßOáOãänSsRUrëyYîïğñòóôõö÷øùúûRrşÿ",

  // klic Lat-Win
  "Çüéâäùæç³ëÕõîÄÆÉÅåôö¼¾ŒœÖÜ£×èáíóú¥¹Êê¬ŸÈº«»----+ÁÂÌª¦¦¬-¯¿¬"
  "L+T+¦+ÃãL-¦T¦=+¤ğĞÏËïÒÍÎì----ŞÙ-ÓßÔÑñòŠšÀÚàÛıİş´­½²¡¢§÷¸°¨ÿûØø¦ ",


  // klic Win-Kam
  "      ÅÅ %›<—†’?        t¨>˜Ÿ‘«ÿóôÏ¤|õùc¸®ªğR½ø+òˆïu ÷¥­¯œñŒ¾"
  "«¶ÆŠ€€¨Ó‰‹×…Ñã¥•§Š™¦—ëšİáª ƒÇ„†‡‡‚©‰ˆ¡ŒƒĞä¤¢“‹”ö©–£û˜îú",

  // klic Win-Lat
  "      ÅÅ %æ<—›¦?        tç>˜œ§«ÿóôÏ¤|õùc¸®ªğR½ø+òˆïu ÷¥­¯•ñ–¾"
  "èµ¶Æ‘€¬¨Ó·Ö×ÒÑãÕàâŠ™üŞéëšíİáê ƒÇ„’†‡Ÿ‚©‰Ø¡ŒÔĞäå¢“‹”öı…£ûìîú",

  // klic Win-Us
  "      ++?%S<STZZ?        ts>stzz ¡¢£¤A|§¨cS«¬­RZ°+²³´u¸as»L½lz"
  "RAAAALCCCEEEEIIDĞNNOOOO×RUUUUYTßraaaalccceeeeiidğnnoooo÷ruuuuyt "
};


/*
Tato pomocna funkce prekoduje cText podle kodu
jenom znaky s ascii hodnotou v horni polovine ASCII tabulky - interval <128,255>
Poznamka : Neni resen problem s posuvnym koncem radku v MEMO polozce !
*/
static void cs_koduj( BYTEP cText, TYPKOD kod)
{
	for ( ; *cText; cText++ )
		if  ( *cText > 127 )
			*cText = kod[ *cText - 128 ] ;
}

/*
Tato pomocna funkce kopiruje cText do cVyst v‡etnˆ '\0'
*/
static void cs_strcpy( BYTEP cVyst, BYTEP cText)
{
	while ( *cVyst++ = *cText++ )
		;
}

/*
Tato pomocna funkce zjistuje, zda v retezci cText je znak znak
*/
static int cs_ischar( BYTE znak,  BYTEP cText)
{
  for ( ; *cText; cText++ )
    if  ( *cText == znak )
      return 1 ;
    return 0;
}


/*
Tato pomocna funkce se pokousi sama identifikovat kodovani retezce cText
*/
static USHORT cs_getkod( BYTEP cText)
{
  BYTEP cSaveText = cText;
  int nKam = 0, nLat = 0, nBoth = 0, nProbl = 0;
  for ( ; *cText; cText++ )
  {
    if ( ( *cText >= 128 ) && ( *cText < 171 ) )  nKam++;
    if ( cs_ischar( *cText, "¬‚Ô„Ò›ŸØ·‘Ö–’µ§¦“”à…éì™šæ•íüœ ¡¢£åÕŞâçıêè" ) )
       nLat++;
    if ( cs_ischar( *cText, "‚„“”™š ¡¢£" ) )
       nBoth++;
    if ( cs_ischar( *cText, "›Ÿ‘–’¦…•œ§" ) )
       nProbl++;
  }
  if ( nKam > nLat  )                               return CS_KOD_KAM;
  else if ( nLat > nKam  )                          return CS_KOD_LAT;
  else if ( ( nLat == nBoth ) && ( nProbl == 0  ) ) return 99;
  else{
        for ( ; *cSaveText; cSaveText++ )
          if ( cs_ischar( *cSaveText, "›Ÿ‘–’¦…•œ§" ) )
                 // Latin 2    v Kameniku je to:
                 //    ›            S hacek
                 //    Ÿ            t hacek
                 //    ‘            z hacek
                 //    –            u krouzek
                 //    ’            Z hacek
                 //    ¦            U krouzek
                 //    …            D hacek
                 //    •            O carka
                 //    œ            L hacek
                 //    §            O striska
              if ( cs_ischar( *cSaveText, "›Ÿ¦…œ§" ) ) nLat++;
                                                  else nKam++;
        if ( nKam > nLat  )      return CS_KOD_KAM;
        else if ( nLat > nKam  ) return CS_KOD_LAT;
  }
  return CS_KOD_LAT;
}

/*

TADY JSOU CLIPPEROVSKE EKVIVALENTY ROZEZNAVACI FUNKCE


static function IsKamenik( c )
    local n := asc(c)
    return n>=128.and.n<=171

static function IsLatin( c )
    return at(c,'¬‚Ô„Ò›ŸØ·‘Ö–’µ§¦“”à…éì™šæ•íüœ ¡¢£åÕŞâçıêè')>0

static function IsBoth( c )
    return at(c,'‚„“”™š ¡¢£')>0

static function IsProblem( c )
    return at(c,'›Ÿ‘–’¦…•œ')>0

static function IsProblKodKam( c )
    return at(c,'Ÿ¦…œ')=0

static function GetKod( cRetez )
    local nLen := len(cRetez), i, cZn, nKam:=0, nLat:=0, nBoth:=0, aProbl:={}
    for i:=1 to nLen
        cZn := substr(cRetez,i,1)
        if IsKamenik(cZn) ; nKam++         ; endif
        if IsLatin(cZn)   ; nLat++         ; endif
        if IsBoth(cZn)    ; nBoth++        ; endif
        if IsProblem(cZn) ; aadd(aProbl,i) ; endif
    next
    if     nKam>nLat  ; return 'K'
    elseif nLat>nKam  ; return 'L'
    elseif nKam=nBoth .and. len(aProbl)=0 ; return 'B'
    else
        nLen := len(aProbl)
        for i:=1 to nLen
            cZn := substr(cRetez, aProbl[i], 1 )
            if IsProblKodKam(substr(cRetez,aProbl[i],1))  ; nKam++
            else                                          ; nLat++
            endif
        next
        if     nKam>nLat  ; return 'K'
        elseif nLat>nKam  ; return 'L'
        endif
    endif
    return 'N'


*/



/*********
*.*********
*. Function: CS()
*  Group: Pomocne funkce
*  SubGroup: Retezce, cestina
*
*. Syntax: Cs( cRetez, [lCesky], [nKodZdroj], [nKodCil] ) --> cKonvertRetez
*.
*. Param.:  cRetez ... retezec ke konverzi - povinny parametr
*.
*.          lCesky ... impl. dle vysledku volani cfg(CFG_CESKY)
*.
*.          nKodZdroj ... kodovani zdrojoveho retezce ( CS_KOD_KAM 0
*.                                                      CS_KOD_LAT 1 )
*.
*.                        impl. dle vysledku volani funkce DejCsKod(),
*.                        ktera se pokusi sama zjistit jeho kodovani
*.
*.          nKodCil ... kodovani ciloveho retezce ( CS_KOD_KAM 0
*.                                                  CS_KOD_LAT 1 )
*.
*.                        impl. dle vysledku volani cfg(CFG_CESKY_KOD)
*.
*.
*********/

CLIPPER cs()
{
   BYTEP cText, cVyst;
   int lCesky =1, nKodText =0, nKodVyst =0;
   EVALINFO info;
   ITEM   retP;

	if ( !( PCOUNT>0 && ISCHAR(1) ) )
	{
    _retc( "cs(), cRetez neni typu C" );
		return;
	}

  cText = _parc(1);
  cVyst = (BYTEP)_xgrab( _parclen(1) + 1 );
  cs_strcpy( cVyst, cText);

	if( ISLOG(2))
		lCesky = _parl(2);
	else
	{
		// Implicitni diakritika vystupu je shodna s diakrit. obrazovky
		//lCesky = cfg(CFG_CESKY)
		_evalNew( &info, _itemPutC( NULL, "CFG" ) );
		_evalPutParam( &info, _itemPutNL( NULL, CFG_CESKY) );
		retP = _evalLaunch( &info ); _evalRelease( &info );

		lCesky = _itemGetL( retP );
		_itemRelease( retP );
	}

	// Kodovani vystupu
	if( ISNUM(4))
    {
        nKodVyst = _parni(4);
        if ( nKodVyst == 99 )
        {
            _evalNew( &info, _itemPutC( NULL, "CFG" ) );
            _evalPutParam( &info, _itemPutNL( NULL, CFG_CESKY_KOD) );
            retP = _evalLaunch( &info ); _evalRelease( &info );

            nKodVyst = (USHORT)_itemGetNL( retP );
            _itemRelease( retP );
        }
    }
	else
	{
		// Implicitni je shodne s kod. obrazovky
		//nKodVyst = cfg(CFG_CESKY_KOD)
		_evalNew( &info, _itemPutC( NULL, "CFG" ) );
		_evalPutParam( &info, _itemPutNL( NULL, CFG_CESKY_KOD) );
		retP = _evalLaunch( &info ); _evalRelease( &info );

		nKodVyst = (USHORT)_itemGetNL( retP );
		_itemRelease( retP );
	}
	// Kodovani textu
	if( ISNUM(3))
		nKodText = _parni(3);
	else
		// Implicitni je shodne s kodovanim vystupu,
		// ale melo by byt shodne s kodovanim databazi
		// Mohl by to byt taky Kamenik ?!
    //
    // nKodText = nKodVyst;
    //
    // opraveno z duvodu nefungovani, pokud nebyl poslan 3.parametr -
    // kodovani se pak nikdy neprovadelo - viz nize - kodovani se provede
    // jen kdyz je   nKodText # nKodVyst => proto udelana zmena - implicitne
    // se bude brat vstupni kod dle CFG_CESKY_KOD
  {
    // _evalNew( &info, _itemPutC( NULL, "CFG" ) );
    // _evalPutParam( &info, _itemPutNL( NULL, CFG_CESKY_KOD) );
    // retP = _evalLaunch( &info ); _evalRelease( &info );

    // nKodText = (USHORT)_itemGetNL( retP );
    // _itemRelease( retP );

    nKodText = cs_getkod( cVyst);
  }

    if ( nKodText == 99  )
         if ( nKodVyst == CS_KOD_WIN )
             nKodText = CS_KOD_LAT;
         else
             nKodText = nKodVyst;

	// Kontrola cisla kodu, coz jsou indexy matice KODY
	if( nKodText <CS_KOD_MIN || nKodText >CS_KOD_MAX
	 || nKodVyst <CS_KOD_MIN || nKodVyst >CS_KOD_MAX )
	{
    _retc( "cs(), nKodText,nKodVyst" );
		return;
	}



	if( !lCesky )

		// Zhozeni diakritiky textu kodovaneho v kodu nKodText
		cs_koduj( cVyst, kody[ nKodText ][ nKodText ]);

	// Pokud je skutecne treba prekodovat
	else if( nKodText != nKodVyst )

		// Zmena kodovani z nKodText na nKodVyst
		cs_koduj( cVyst, kody[ nKodText ][ nKodVyst ]);

	// Vr t¡me jako v˜sledek fce
	_retc(  cVyst );
	_xfree( cVyst );
  return;
}


/*********
*.*********
*. Function: DejCskod()
*  Group: Pomocne funkce
*  SubGroup: Retezce, cestina
*
*. Syntax: DejCskod( cRetez ) --> nKod
*.
*. Param.:  cRetez ... retezec ke zjistovani kodu - povinny parametr
*.
*. Return :  nKod  ...  funkce se pokusi zjistit typ kodovani vstupniho
*.                      retezce. Zatim rozeznava KAMENIKa a LATIN2
*.                      Takze vraci
*.                                    CS_KOD_KAM   0
*.                                    CS_KOD_LAT   1
*.
*.                                nebo
*.
*.                                    99, coz znamena ze retezec vyhovuje
*.                                        obema kodovani (resp. obsahuje
*.                                        jen ty znaky, ktere jsou vyznamem
*.                                        spolecne v obou kodovani
*.
*********/


CLIPPER DejCsKod()
{

  if ( !( ISCHAR(1) ) )
	{
    _retc( "Chyba par. DejKod() - neni poslan retezec" );
		return;
	}

  _retni( cs_getkod( _parc(1) ) );
  return;
}
