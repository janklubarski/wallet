Ke spuštění aplikace (backend) použijte příkaz (z této složky - rootu projektu):

docker run -p 8080:8080 wallet-app

Ke spuštění frontendu použijte příkaz (ze složky src/frontend ):

npm run dev

Jakmile aplikace nastartuje, jděte na toto url:

http://localhost:5173/ -> zde běží frontend aplikace.

Pak už se jen stačí registrovat (jméno, heslo....) a vytvořit peněženku (peněženky).

Je možné "peníze" nabít (load), stáhnout (převod na "bankovní účet"), či převést mezi existujícími peněženky (je možné převést jen stejnou měnu a na existující peněženku - je jedno, jaký uživatel danou peněženku má).

A to je pro tento mini projektík vše.