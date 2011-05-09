## triprotic acid/strong base

function triproticacid (Ca,Cb,Va,Vb,K1,K2,K3)
Kw = 1e-14;

## solve 5th-order eqn for H: a*H^5 + b*H^4 + c*H^3 + d*H^2 + e*H + f = 0
a = Va + Vb;
b = Cb.*Vb + Va.*K1 + Vb.*K1;
c = -Ca.*Va.*K1 + Cb.*Vb.*K1 + Va.*K1.*K2 + Vb.*K1.*K2 - Va.*Kw - Vb.*Kw;
d = -2*Ca.*Va.*K1.*K2 + Cb.*Vb.*K1.*K2 + Va.*K1.*K2.*K3 + Vb.*K1.*K2.*K3 - Va.*K1.*Kw - Vb.*K1.*Kw;
e = -3*Ca.*Va.*K1.*K2.*K3 + Cb.*Vb.*K1.*K2.*K3 - Va.*K1.*K2.*Kw - Vb.*K1.*K2.*Kw;
f = -Va.*K1.*K2.*K3.*Kw - Vb.*K1.*K2.*K3.*Kw;

for indx = 1:length(Vb)
	x = roots([a(indx), b(indx), c(indx), d(indx), e(indx), f(indx)]);
	x = sort(x);
	H1(indx) = x(1);
	H2(indx) = x(2);
	H3(indx) = x(3);
	H4(indx) = x(4);
	H5(indx) = x(5);
end

## find pH at each Vb value
#pH1 = -log10(H1);
#pH2 = -log10(H2);
#pH3 = -log10(H3);
#pH4 = -log10(H4);
pH5 = -log10(H5);

## plot pH versus Vb
#plot(Vb,pH1);
#plot(Vb,pH2);
#plot(Vb,pH3);
#plot(Vb,pH4);
plot(Vb,pH5); axis ([0,100,0,14]); xlabel('Vb'); ylabel('pH');

endfunction