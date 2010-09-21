## weak acid/strong base

function weakacid (Ca,Cb,Va,Vb,Ka)
Kw = 1e-14;

## solve cubic eqn for H: a*H^3 + b*H^2 + c*H + d = 0
a = Va + Vb;
b = Cb.*Vb + Va.*Ka + Vb.*Ka;
c = Cb.*Vb.*Ka - Ca.*Va.*Ka - Va.*Kw - Vb.*Kw;
d = -Kw.*Ka.*(Va + Vb);

for indx = 1:length(Vb)
	x = roots([a(indx), b(indx), c(indx), d(indx)]);
	x = sort(x);
	H1(indx) = x(1);
	H2(indx) = x(2);
	H3(indx) = x(3);
end

## find pH at each Vb value
#pH1 = -log10(H1);
#pH2 = -log10(H2);
pH3 = -log10(H3);

## plot pH versus Vb
#plot(Vb,pH1);
#plot(Vb,pH2);
plot(Vb,pH3); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');

endfunction