#
# Copyright (C) 2009 Samir Chekkal
# This program is distributed under the terms of the GNU General Public License.
#

function level(shift) {
  for(i=0;i<shift;i++){
    printf " " 
  }
}
BEGIN {
  FS="->"; shift = 0;
}
/->/{
  reverse = 0;
  arrow = "+--->";
  if(index($2,"dashed") > 0){
    reverse = 1;
  }
  methodName = substr($2,index($2,"label=\"")+7);
  methodName=substr(methodName,0,index(methodName,"\"")-1);
  $2=substr($2,0,index($2,"[")-1);
  head = $1;
  tail = $2;
  if(reverse){
    arrow = "<---+";
    head = $2;
    tail = $1;
  }
  if(reverse) {
    shift -= length(head arrow);
  } 
  level(shift);
  printf head;
  printf arrow;
  printf tail;
  printf "  ("methodName")\n";
  
  if(!reverse) {
    shift += length(head arrow);
  } 
}
