## diprotic acid/strong base

function diproticacid (Ca,Cb,Va,Vb,K1,K2)
Kw = 1e-14;

## solve quartic eqn for H: a*H^4 + b*H^3 + c*H^2 + d*H + e = 0
a = Va + Vb;
b = Cb.*Vb + Va.*K1 + Vb.*K1;
c = Cb.*Vb.*K1 - Ca.*Va.*K1 + Va.*K1.*K2 + Vb.*K1.*K2 - Va.*Kw - Vb.*Kw;
d = Cb.*Vb.*K1.*K2 - 2*Ca.*Va.*K1.*K2 - Va.*K1.*Kw - Vb.*K1.*Kw;
e = -(K1.*K2.*Kw).*(Va + Vb);

for indx = 1:length(Vb)
	x = roots([a(indx), b(indx), c(indx), d(indx), e(indx)]);
	x = sort(x);
	H1(indx) = x(1);
	H2(indx) = x(2);
	H3(indx) = x(3);
	H4(indx) = x(4);
end

## find pH at each Vb value
#pH1 = -log10(H1);
#pH2 = -log10(H2);
#pH3 = -log10(H3);
pH4 = -log10(H4);

## plot pH versus Vb
#plot(Vb,pH1);
#plot(Vb,pH2);
#plot(Vb,pH3);
plot(Vb,pH4); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');

endfunction